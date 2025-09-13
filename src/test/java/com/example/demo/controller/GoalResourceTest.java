package com.example.demo.controller;

import com.example.demo.model.Goal;
import com.example.demo.model.User;
import com.example.demo.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalResource.class)
class GoalResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private GoalService goalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getGoal_existingGoal_returns200() throws Exception {
        Goal goal = new Goal("Finish project", new User());
        goal.setId(1L);
        goal.setDueDate(LocalDateTime.now());

        when(goalService.getGoalById(1L)).thenReturn(goal);

        mockMvc.perform(get("/goal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Finish project"));
    }

    @Test
    void getGoal_nonExistingGoal_returns400() throws Exception {
        when(goalService.getGoalById(99L)).thenThrow(new IllegalArgumentException("no goal with such ID"));

        mockMvc.perform(get("/goal/99"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createGoal_valid_returns200() throws Exception {
        Goal request = new Goal("New Goal", new User());
        Goal saved = new Goal("New Goal", new User());
        saved.setId(1L);

        when(goalService.createGoal(any(Goal.class), eq(42L))).thenReturn(saved);

        mockMvc.perform(post("/goal/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Goal"));
    }

    @Test
    void createGoal_invalidUser_returns400() throws Exception {
        Goal request = new Goal("Invalid Goal", new User());

        when(goalService.createGoal(any(Goal.class), eq(99L)))
                .thenThrow(new IllegalArgumentException("userId should be a valid Id"));

        mockMvc.perform(post("/goal/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllGoals_returnsList() throws Exception {
        Goal g1 = new Goal("Goal A", new User());
        g1.setId(1L);
        Goal g2 = new Goal("Goal B", new User());
        g2.setId(2L);

        List<Goal> goals = Arrays.asList(g1, g2);

        when(goalService.getGoals()).thenReturn(goals);

        mockMvc.perform(get("/goal/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Goal A"));
    }

    @Test
    void updateGoal_valid_returns200() throws Exception {
        Goal updateRequest = new Goal("Updated Goal", new User());
        Goal updated = new Goal("Updated Goal", new User());
        updated.setId(1L);

        when(goalService.updateGoal(eq(1L), any(Goal.class))).thenReturn(updated);

        mockMvc.perform(put("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Goal"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateGoal_invalid_returns400() throws Exception {
        Goal invalid = new Goal("Invalid", new User());

        when(goalService.updateGoal(eq(1L), any(Goal.class)))
                .thenThrow(new IllegalArgumentException("goal must already be in the db"));

        mockMvc.perform(put("/goal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteGoal_existing_returns204() throws Exception {
        doNothing().when(goalService).deleteById(1L);

        mockMvc.perform(delete("/goal/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteGoal_nonExisting_returns400() throws Exception {
        doThrow(new IllegalArgumentException("goal must already be in the db"))
                .when(goalService).deleteById(99L);

        mockMvc.perform(delete("/goal/99"))
                .andExpect(status().isBadRequest());
    }
}