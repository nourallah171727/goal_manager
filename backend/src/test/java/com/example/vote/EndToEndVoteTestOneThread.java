package com.example.vote;

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
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class EndToEndVoteTestOneThread {

    @Autowired
    private MockMvc mockMvc;
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
    @PersistenceContext
    private EntityManager entityManager;




    @Test
    @WithMockUser(username = "voter", roles = {"USER"})
    void basicVote() throws Exception{
        User uploader =new User("name","email","password");
        uploader.setRole("USER");
        userRepository.save(uploader);

        User voter=new User("voter","voter","password");
        voter.setRole("USER");
        userRepository.save(voter);
        
        Goal goal=new Goal("name", uploader);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(uploader);
        goal.getMembers().add(voter);
        goalRepository.save(goal);


        Task task =new Task("task",goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);

        // given
        long taskId = task.getId();
       uploadRepository.save(new Upload(uploader.getId(),taskId,"someFilePath",1));
       entityManager.flush();
       entityManager.clear();

       mockMvc.perform(post("/votes/{taskId}/uploader/{votedUserId}", taskId, uploader.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Upload uploadAfterVote = uploadRepository.findById(new UploadId(uploader.getId(),taskId))
                .orElseThrow(() -> new AssertionError("Upload not found after vote"));

        assertEquals(2, uploadAfterVote.getCurrentVotes(),
                "Vote count should increment by 1 after voting");
    }
    @WithMockUser(username = "voter", roles = {"USER"})
    @Test
    void voteResultingInATaskMarkDone() throws Exception{
        User uploader =new User("name","email","password");
        uploader.setRole("USER");
        userRepository.save(uploader);

        User voter=new User("voter","voter","password");
        voter.setRole("USER");
        userRepository.save(voter);

        Goal goal=new Goal("name", uploader);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(uploader);
        goal.getMembers().add(voter);
        goal.setTotalPoints(10);
        goalRepository.save(goal);


        Task task =new Task("task",goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);

        // given
        long taskId = task.getId();
        uploadRepository.save(new Upload(uploader.getId(),taskId,"someFilePath",2));
        mockMvc.perform(post("/votes/{taskId}/uploader/{votedUserId}", taskId, uploader.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        entityManager.flush();
        entityManager.clear();
        User dbUploader=userRepository.findById(uploader.getId()).orElseThrow();
        assertTrue( uploadRepository.findById(new UploadId(uploader.getId(),taskId)).isEmpty());
        assertTrue(dbUploader.getFinishedTasks().contains(task));
        assertEquals(userScorePairRepository.findById(new UserGoalScorePairId(uploader.getId(),goal.getId())).orElseThrow().getScore(),10);



    }
}
