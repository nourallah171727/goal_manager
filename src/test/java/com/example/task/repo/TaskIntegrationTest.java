package com.example.task.repo;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalStand;
import com.example.goal.entity.Goal;
import com.example.task.entity.Task;
import com.example.user.entity.User;
import com.example.goal.repo.GoalRepository;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Iterator;
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
        user = new User("hacker12.5", "thisisanemail@gmail.de","some password");
        user.setRole("USER");
        userRepository.save(user);
        entityManager.flush();

        goal = new Goal("get 1.0", user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setGoalStand(GoalStand.PROGRESS);
        task = new Task("learn the course", goal);
        goalRepository.save(goal);
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
        assertNotNull(taskDB.get().getGoal().getHost());
        assertEquals(user.getUsername(), taskDB.get().getGoal().getHost().getUsername());
        assertEquals(user.getEmail(), taskDB.get().getGoal().getHost().getEmail());

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


    @Test
    void saveMultipleTasks(){
        Task task2 = new Task("attend the lectures", goal);
        taskRepository.save(task2);
        entityManager.flush();

        entityManager.clear();
        Optional<Goal> goalDB = goalRepository.findById(goal.getId());
        assertTrue(goalDB.isPresent());
        Set<Task> taskGoalDB = goalDB.get().getTasks();
        assertNotNull(taskGoalDB);
        assertEquals(2, taskGoalDB.size());
        Iterator<Task> it = taskGoalDB.iterator();
        Task retrievedTask = it.next();
        assertNotNull(retrievedTask);
        Task retrievedTask2 = it.next();
        assertNotNull(retrievedTask2);
        assertTrue(task.getName().equals(retrievedTask.getName()) ^task.getName().equals(retrievedTask2.getName()));
        assertTrue(task2.getName().equals(retrievedTask.getName()) ^task2.getName().equals(retrievedTask2.getName()));
        assertEquals(goalDB.get().getName(), retrievedTask.getGoal().getName());
        assertEquals(goalDB.get().getName(), retrievedTask2.getGoal().getName());
    }

}