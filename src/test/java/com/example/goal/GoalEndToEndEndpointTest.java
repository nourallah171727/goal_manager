package com.example.goal;


import com.example.dto.goal.GoalCreateDTO;
import com.example.dto.task.TaskCreateDTO;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.repo.GoalRepository;
import com.example.task.common.TaskDifficulty;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoalEndToEndEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Test
    @WithMockUser(username = "bibouba",roles = {"USER"})
    void createPublicGoal_shouldSucceedAndReturn200() throws Exception {
        // arrange
        User host = new User("bibouba", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        GoalCreateDTO goalDto = new GoalCreateDTO(
                "Public Goal",
                LocalDate.now().plusDays(10),
                GoalCategory.SPORTS,
                GoalType.PUBLIC,
                null,
                3,
                Set.of(new TaskCreateDTO("Run 5km", TaskDifficulty.EASY))
        );

        // act & assert
        mockMvc.perform(post("/goal/{userId}", host.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Public Goal"))
                .andExpect(jsonPath("$.category").value("SPORTS"));
    }
}
