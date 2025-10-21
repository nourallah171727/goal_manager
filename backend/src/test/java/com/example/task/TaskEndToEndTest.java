package com.example.task;

import com.example.dto.task.TaskCreateDTO;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.task.common.TaskDifficulty;
import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class TaskEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // -----------------------------------------------------
    // POST /task/{goalId} -> create new task
    // -----------------------------------------------------
    @Test
    @WithMockUser(username = "host", roles = {"USER"})
    void createTask_shouldSucceedAndReturn200() throws Exception {
        User host = new User("host", "host@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Fitness Goal", host);
        goal.setDueDate(LocalDate.now().plusDays(10));
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        TaskCreateDTO dto = new TaskCreateDTO("Run 10km", TaskDifficulty.MEDIUM);

        mockMvc.perform(post("/task/{goalId}", goal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Run 10km"))
                .andExpect(jsonPath("$.difficulty").value("MEDIUM"));

        List<Task> tasks = taskRepository.findByGoal_Id(goal.getId());
        assertEquals(1, tasks.size());
    }

    // -----------------------------------------------------
    // GET /task/{taskId} -> fetch single task
    // -----------------------------------------------------
    @Test
    @WithMockUser(username = "anyuser", roles = {"USER"})
    void getTask_shouldReturnTaskDetails() throws Exception {
        User host = new User("host", "h@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Study Goal", host);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        Task task = new Task("Read 50 pages", goal);
        task.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.save(task);

        mockMvc.perform(get("/task/{taskId}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Read 50 pages"));
    }

    // -----------------------------------------------------
    // GET /task/all/{goalId} -> fetch all tasks for a goal
    // -----------------------------------------------------
    @Test
    @WithMockUser(username = "anyuser", roles = {"USER"})
    void getAllTasks_shouldReturnListOfTasks() throws Exception {
        User host = new User("host", "h@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Project Goal", host);
        goal.setCategory(GoalCategory.WORK);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        Task t1 = new Task("Implement feature A",  goal);
        t1.setDifficulty(TaskDifficulty.DIFFICULT);
        Task t2 = new Task("Write documentation", goal);
        t2.setDifficulty(TaskDifficulty.DIFFICULT);
        taskRepository.saveAll(List.of(t1, t2));

        mockMvc.perform(get("/task/all/{goalId}", goal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Implement feature A"))
                .andExpect(jsonPath("$[1].name").value("Write documentation"));
    }

    // -----------------------------------------------------
    // PUT /task/{taskId}?name=NewName -> update task name
    // -----------------------------------------------------
    @Test
    @WithMockUser(username = "host", roles = {"USER"})
    void updateTaskName_shouldUpdateAndReturn200() throws Exception {
        User host = new User("host", "h@email.com", "password");
        host.setRole("USER");
        userRepository.save(host);

        Goal goal = new Goal("Coding Goal", host);
        goal.setCategory(GoalCategory.WORK);
        goal.setType(GoalType.PUBLIC);
        goalRepository.save(goal);

        Task task = new Task("Initial Name", goal);
        task.setDifficulty(TaskDifficulty.EASY);
        taskRepository.save(task);

        mockMvc.perform(put("/task/{taskId}", task.getId())
                        .param("name", "Refactored Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Refactored Name"));

        entityManager.flush();
        entityManager.clear();

        Task updated = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals("Refactored Name", updated.getName());
    }
}
