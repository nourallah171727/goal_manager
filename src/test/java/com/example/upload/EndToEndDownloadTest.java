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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class EndToEndDownloadTest {
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
    @Test
    @WithMockUser(username = "name", roles = {"USER"})
    void shouldDOWNLoadFileSuccessfully() throws Exception {
        User uploader=new User("name","email","password");
        uploader.setRole("USER");
        userRepository.save(uploader);
        User downloader=new User("name1","email1","password");
        downloader.setRole("USER");
        userRepository.save(downloader);

        Goal goal=new Goal("name",uploader);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setVotesToMarkCompleted(3);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(uploader);
        goal.getMembers().add(downloader);
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

        assertTrue(Files.exists(uploadsDir));

        boolean fileExists = Files.list(uploadsDir)
                .anyMatch(p -> p.getFileName().toString().contains("sample.pdf"));
        assertTrue(fileExists,"Uploaded file should exist in 'uploads' directory");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.
                get("/files/{userId}/download/{taskId}", uploader.getId(), task.getId())
                                .accept(MediaType.APPLICATION_OCTET_STREAM)
                )
                .andExpect(status().isOk())
                .andReturn();

        // --- Step 3: Validate response content ---
        byte[] downloadedBytes = result.getResponse().getContentAsByteArray();
        byte[] originalBytes = mockFile.getBytes();

        assertArrayEquals(originalBytes, downloadedBytes,
                "Downloaded file content should match the uploaded file content");
    }
}
