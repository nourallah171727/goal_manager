package com.example.feed;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.goal.service.GoalService;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoalPopularityBasedEndToEndTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private GoalRepository goalRepository;
    @Autowired private GoalService goalService;
    @Autowired private FeedServiceFactory feedServiceFactory;
    @PersistenceContext private EntityManager entityManager;
    @Test
    @WithMockUser(username = "u1",roles = {"USER"})
    void testFeedReturnsGoalsOrderedByPopularity() throws Exception {
        User host = new User("host", "h@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal g1 = new Goal("Goal 1", host);
        g1.setType(GoalType.PUBLIC);
        g1.setCategory(GoalCategory.SPORTS);
        g1.setCreatedAt(LocalDateTime.now());

        Goal g2 = new Goal("Goal 2", host);
        g2.setType(GoalType.PUBLIC);
        g2.setCategory(GoalCategory.SPORTS);
        g2.setCreatedAt(LocalDateTime.now());


        Goal g3 = new Goal("Goal 3", host);
        g3.setType(GoalType.PUBLIC);
        g3.setCategory(GoalCategory.SPORTS);
        g3.setCreatedAt(LocalDateTime.now());



        User u1 = new User("u1", "u1@email.com", "pw");
        u1.setRole("USER");
        User u2 = new User("u2", "u2@email.com", "pw");
        u2.setRole("USER");
        User u3 = new User("u3", "u3@email.com", "pw");
        u3.setRole("USER");
        User u4 = new User("u4", "u4@email.com", "pw");
        u4.setRole("USER");
        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);


        g1.getMembers().add(u1);
        g2.getMembers().add(u1);
        g2.getMembers().add(u2);
        g3.getMembers().add(u1);
        g3.getMembers().add(u2);
        g3.getMembers().add(u3);
        g3.getMembers().add(u4);
        goalRepository.saveAll(List.of(g1, g2, g3));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/feed/" + u1.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .param("type", "popularityBased"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Goal 3"))
                .andExpect(jsonPath("$.content[1].name").value("Goal 2"))
                .andExpect(jsonPath("$.content[2].name").value("Goal 1"));

    }
}
