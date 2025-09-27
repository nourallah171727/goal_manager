package com.example.task.controller;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalStand;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user_nvrntb", roles = {"USER"})
    void createTask_underExistingGoal_returnsTask() throws Exception {
        // Arrange: first create a user and goal in the DB
        User user =userRepository.findById(21332L).orElseThrow();

        Goal goal = new Goal();
        goal.setName("My First Goal");
        goal.setCategory(GoalCategory.HEALTH);
        goal.setType(GoalType.PUBLIC);
        goal.setHost(user);
        goal.setGoalStand(GoalStand.PROGRESS);
        goalRepository.save(goal);

        // Build the task payload
        Map<String, Object> payload = Map.of(
                "name", "First Task",
                "difficulty", "EASY"
        );
        String json = objectMapper.writeValueAsString(payload);

        // Act + Assert
        mockMvc.perform(post("/task/{goalId}", goal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("First Task"))
                .andExpect(jsonPath("$.difficulty").value("EASY"));
    }}