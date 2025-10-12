package com.example.vote;
import org.hibernate.Hibernate;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.ranking.model.UserGoalScorePairId;
import com.example.ranking.repo.UserScorePairRepository;
import com.example.task.common.TaskDifficulty;
import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import com.example.upload.repo.UploadRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteServiceMultiThreaded {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserScorePairRepository userScorePairRepository;

    @Autowired
    private UploadRepository uploadRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void concurrentVotingResultsInSingleTaskMarkDone() throws Exception {
        // --- Setup phase
        User uploader = new User("name", "email", "password");
        uploader.setRole("USER");
        userRepository.save(uploader);

        User voter1 = new User("voter1", "voter1", "password");
        voter1.setRole("USER");
        userRepository.save(voter1);

        User voter2 = new User("voter2", "voter2", "password");
        voter2.setRole("USER");
        userRepository.save(voter2);

        User voter3 = new User("voter3", "voter3", "password");
        voter3.setRole("USER");
        userRepository.save(voter3);

        Goal goal = new Goal("goal", uploader);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.setTotalPoints(10);
        goal.getMembers().add(uploader);
        goal.getMembers().addAll(List.of(voter1, voter2, voter3));
        goalRepository.save(goal);

        Task task = new Task("task", goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);

        long taskId = task.getId();
        uploadRepository.save(new Upload(uploader.getId(), taskId, "someFilePath", 0));
        // --- Concurrent execution phase
        List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());
        int threads = 3;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<User> voters = List.of(voter1, voter2, voter3);

        for (User voter : voters) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(voter.getUsername(), voter.getPassword(),
                                    List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    );

                    mockMvc.perform(post("/votes/{taskId}/uploader/{votedUserId}", taskId, uploader.getId())
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());


                } catch (Throwable t) {
                    failures.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        if (!failures.isEmpty()) {
            for (Throwable t : failures) t.printStackTrace();
            fail("One or more threads failed: " + failures.size());
        }

        UploadId uploadId = new UploadId(uploader.getId(), taskId);
        assertTrue(uploadRepository.findById(uploadId).isEmpty());

        transactionTemplate.executeWithoutResult(status -> {
            User dbUploader = userRepository.findById(uploader.getId()).orElseThrow();
            assertTrue(dbUploader.getFinishedTasks().contains(task));
        });

        var scorePair = userScorePairRepository.findById(
                new UserGoalScorePairId(uploader.getId(), goal.getId())
        ).orElseThrow();

        assertEquals(10, scorePair.getScore(), "Uploader score should be 10 (computed only once)");}
}
