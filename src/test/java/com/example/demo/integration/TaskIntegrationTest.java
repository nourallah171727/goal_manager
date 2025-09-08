package com.example.demo.integration;

import com.example.demo.model.Goal;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private Goal goal;
    private Task task;

    @BeforeEach
    void setup(){
        user = new User("hacker12.5", "thisisanemail@gmail.de");
        userRepository.save(user);
        entityManager.flush();

        goal = new Goal("get 1.0", user);
        goalRepository.save(goal);
        entityManager.flush();

        task = new Task("learn the course", goal);
        taskRepository.save(task);
        entityManager.flush();
    }

    @Test
    void saveTask(){
        //correctness of the task object
        entityManager.clear();
        Optional<Task> taskDB = taskRepository.findById(task.getId());
        assertTrue(taskDB.isPresent());
        assertEquals(task.getName(), taskDB.get().getName());
        assertNotNull(taskDB.get().getGoal());
        assertEquals(goal.getName(), taskDB.get().getGoal().getName());
        assertNotNull(taskDB.get().getGoal().getUser());
        assertEquals(user.getUsername(), taskDB.get().getGoal().getUser().getUsername());
        assertEquals(user.getEmail(), taskDB.get().getGoal().getUser().getEmail());

        //correctness of the goal object
        entityManager.clear();
        Optional<Goal> goalDB = goalRepository.findById(goal.getId());
        assertTrue(goalDB.isPresent());
        Set<Task> taskGoalDB = goalDB.get().getTasks();
        assertNotNull(taskGoalDB);
        assertEquals(1, taskGoalDB.size());
        Task firstTask = taskGoalDB.iterator().next();
        assertNotNull(firstTask);
        assertEquals(task.getName(), firstTask.getName());
        Goal firstTaskGoal = firstTask.getGoal();
        assertNotNull(firstTaskGoal);
        assertEquals(goalDB.get().getName(),firstTaskGoal.getName());
    }

    
}
