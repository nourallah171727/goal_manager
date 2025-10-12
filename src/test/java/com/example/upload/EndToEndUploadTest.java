package com.example.upload;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.task.common.TaskDifficulty;
import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
import com.example.upload.repo.UploadRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class EndToEndUploadTest {
    @Autowired
    private MockMvc mockMvc;

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

    private final Path uploadsDir = Paths.get("uploads");

    @AfterEach
    void cleanUp() throws IOException {
        if (Files.exists(uploadsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadsDir)) {
                for (Path file : stream) {
                    Files.deleteIfExists(file);
                }
            }
            Files.deleteIfExists(uploadsDir);
        }
        uploadRepository.deleteAll();
    }
    //not authenticated check
    @Test
    void notAuthenticated() throws Exception {
        User user=new User("name","email","password");
        user.setRole("USER");
        userRepository.save(user);

        Goal goal=new Goal("name",user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(user);
        goalRepository.save(goal);


        Task task =new Task("task",goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "sample.pdf", "application/pdf",
                "%PDF-1.4\n%âãÏÓ\n1 0 obj\n<<>>\nendobj\n".getBytes()
        );
        Long taskId=task.getId();
        // when
        mockMvc.perform(
                        multipart("/files/upload/{task_id}", taskId)
                                .file(mockFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "name", roles = {"USER"})
    void shouldUploadFileSuccessfully() throws Exception {
        User user=new User("name","email","password");
        user.setRole("USER");
        userRepository.save(user);

        Goal goal=new Goal("name",user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(user);
        goalRepository.save(goal);


        Task task =new Task("task",goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);

        // given
        long taskId = task.getId();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "sample.pdf", "application/pdf",
                "%PDF-1.4\n%âãÏÓ\n1 0 obj\n<<>>\nendobj\n".getBytes()
        );

        // when
        mockMvc.perform(
                        multipart("/files/upload/{task_id}", taskId)
                                .file(mockFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("File uploaded successfully")));

        // then
        assertTrue(Files.exists(uploadsDir));

        boolean fileExists = Files.list(uploadsDir)
                .anyMatch(p -> p.getFileName().toString().contains("sample.pdf"));
        assertTrue(fileExists,"Uploaded file should exist in 'uploads' directory");
    }
}
