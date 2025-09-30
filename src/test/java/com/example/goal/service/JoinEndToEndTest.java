package com.example.goal.service;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalStand;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class JoinEndToEndTest{
    // prepare user in DB
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @WithMockUser(username = "user_icygpazdadaaaa", roles = {"USER"})
    @Test
    void userCanJoinGoal() throws Exception {
        // 1. prepare user in DB (id will be auto-generated)
        User user = new User();
        user.setUsername("user_icygpazdadaaaa");   // must match @WithMockUser
        user.setEmail("user_icygpazddaaa@mail.com");
        user.setPassword("{noop}pwd12345"); // use your encoder here if not noop
        user.setRole("ROLE_USER");
        user = userRepository.save(user);

        Long userId = user.getId();

        // 2. prepare goal in DB
        Goal goal = new Goal();
        goal.setName("Test Goal");
        goal.setCategory(GoalCategory.HEALTH);
        goal.setType(GoalType.PUBLIC);
        goal.setVotesToMarkCompleted(1);
        goal.setGoalStand(GoalStand.PROGRESS);
        goal.setHost(user);
        goal = goalRepository.save(goal);

        Long goalId = goal.getId();

        // 3. perform join request (authenticated by @WithMockUser)
        mockMvc.perform(post("/goal/{goalId}/join/{userId}", goalId, userId))
                .andExpect(status().isOk());
    }

}
