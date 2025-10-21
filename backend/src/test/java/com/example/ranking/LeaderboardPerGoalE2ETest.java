package com.example.ranking;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.ranking.model.UserGoalScorePair;
import com.example.ranking.repo.UserScorePairRepository;
import com.example.ranking.service.GoalLeaderboardService;
import com.example.ranking.service.UserScorePairService;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
@SpringBootTest
@AutoConfigureMockMvc

public class LeaderboardPerGoalE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserScorePairRepository userScorePairRepository;

    @Autowired
    private UserScorePairService userScorePairService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalLeaderboardService leaderboardService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Test
    @WithMockUser(username = "tester", roles = {"USER"})
    void getTopK_shouldReturnTopUsersSortedByScore() throws Exception {
        // Arrange
        User host = new User("host", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Leaderboard Goal", host);
        goal.setDueDate(LocalDate.now().plusDays(5));
        goal.setCategory(GoalCategory.WORK);
        goal.setType(GoalType.PUBLIC);
        goal.setTotalPoints(10);
        goalRepository.save(goal);

        // Create 6 users with scores 1 → 6
        for (int i = 1; i <= 6; i++) {
            User user = new User("user" + i, "user" + i + "@mail.com", "pw");
            user.setRole("USER");
            userRepository.save(user);
            userScorePairService.joinGoal(goal.getId(), user.getId());
            userScorePairRepository.incrementScore(goal.getId(), user.getId(),i);
        }
        entityManager.flush();
        entityManager.clear();




        String responseJson = mockMvc.perform(
                        get("/leaderboard/{goalId}/top", goal.getId())
                                .param("k", "8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<UserGoalScorePair> topList =
                objectMapper.readValue(responseJson, new TypeReference<>() {});


        assertThat(topList).hasSize(6);
        assertThat(topList.get(0).getScore()).isEqualTo(6);
        assertThat(topList.get(5).getScore()).isEqualTo(1);

        for (int i = 0; i < topList.size() - 1; i++) {
            assertThat(topList.get(i).getScore())
                    .isGreaterThanOrEqualTo(topList.get(i + 1).getScore());
        }
    }
}
