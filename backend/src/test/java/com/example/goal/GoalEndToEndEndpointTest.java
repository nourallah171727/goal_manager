package com.example.goal;


import com.example.dto.goal.GoalCreateDTO;
import com.example.dto.task.TaskCreateDTO;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    @Test
    @WithMockUser(username = "host", roles = {"USER"})
    void deleteGoal_shouldSucceedForHost() throws Exception {
        User host = new User("host", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Goal to delete", host);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        mockMvc.perform(delete("/goal/{id}", goal.getId()))
                .andExpect(status().isNoContent());

        assertFalse(goalRepository.findById(goal.getId()).isPresent(),
                "Goal should be deleted from repository");
    }
    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void joinGoal_shouldAddUserToGoalMembers() throws Exception {
        User host = new User("host", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        User user = new User("alice", "alice@email.com", "password");
        user.setRole("USER");
        userRepository.save(user);

        Goal goal = new Goal("Joinable Goal", host);
        goal.setDueDate(LocalDate.now());
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        mockMvc.perform(post("/goal/{goalId}/join/{userId}", goal.getId(), user.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        Goal updated = goalRepository.findById(goal.getId()).orElseThrow();
        assertTrue(updated.getMembers().stream()
                        .anyMatch(m -> m.getUsername().equals("alice")),
                "User should have joined the goal");
    }
    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void leaveGoal_shouldRemoveUserFromMembers() throws Exception {
        // arrange
        User host = new User("host", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        User user = new User("bob", "bob@email.com", "password");
        user.setRole("USER");
        userRepository.save(user);

        Goal goal = new Goal("Leavable Goal", host);
        goal.setDueDate(LocalDate.now());
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goal.getMembers().add(user);
        goalRepository.save(goal);

        // act
        mockMvc.perform(delete("/goal/{goalId}/leave/{userId}", goal.getId(), user.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        Goal updated = goalRepository.findById(goal.getId()).orElseThrow();
        assertFalse(updated.getMembers().stream()
                        .anyMatch(m -> m.getUsername().equals("bob")),
                "User should have been removed from the goal members");
    }
}
