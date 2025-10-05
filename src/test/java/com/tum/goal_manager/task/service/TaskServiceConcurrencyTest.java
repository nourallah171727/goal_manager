package com.tum.goal_manager.task.service;
import com.tum.goal_manager.goal.common.GoalCategory;
import com.tum.goal_manager.goal.common.GoalStand;
import com.tum.goal_manager.goal.common.GoalType;
import com.tum.goal_manager.goal.entity.Goal;
import com.tum.goal_manager.goal.repo.GoalRepository;
import com.tum.goal_manager.ranking.model.UserGoalScorePair;
import com.tum.goal_manager.ranking.repo.UserScorePairRepository;
import com.tum.goal_manager.task.common.TaskDifficulty;
import com.tum.goal_manager.task.entity.Task;
import com.tum.goal_manager.task.repo.TaskRepository;
import com.tum.goal_manager.user.entity.User;
import com.tum.goal_manager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TaskServiceConcurrencyTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserScorePairRepository userScorePairRepository;

    private User user;
    private Goal goal;
    private List<Task> tasks;
    @BeforeAll
    static void setupSecurityStrategy() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
    @BeforeEach
    void setup() {
        user = new User("testuser1d53dsaazefzazfzzad", "test@examplzfzfeefded2a12a3adz3.com", "pw");
        user.setRole("USER");
        userRepository.save(user);

        goal = new Goal("goal", user);
        goal.setGoalStand(GoalStand.PROGRESS);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);
        tasks=new ArrayList<>(10);
        for(int i=0;i<10;i++){
            Task task = new Task("task " + i, goal);
            task.setDifficulty(TaskDifficulty.DIFFICULT);
            taskRepository.save(task);
            tasks.add(task);
        }
        // make user member of goal
        goal.getMembers().add(user);
        user.getGoals().add(goal);
        goalRepository.save(goal);
        userRepository.save(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void concurrentMarkDone() throws InterruptedException {
        int threadCount = 10; // simulate 10 concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int taskIndex = i;
            executor.submit(() -> {
                try {
                    taskService.markDone(tasks.get(taskIndex).getId());
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserGoalScorePair usp = userScorePairRepository.findByGoalIdAndUserId(goal.getId(), user.getId())
                .orElseThrow();

        System.out.println("Final score = " + usp.getScore());
        assertEquals(100, usp.getScore(),
                "Lost update! Concurrency issue detected.");
    }
}