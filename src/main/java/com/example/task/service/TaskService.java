package com.example.task.service;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.task.entity.Task;
import com.example.task.common.TaskStatus;
import com.example.user.entity.User;
import com.example.task.repo.TaskRepository;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    @Autowired
    public TaskService(TaskRepository repository,UserRepository userRepository,GoalRepository goalRepository){
        this.taskRepository = repository;
        this.userRepository=userRepository;
        this.goalRepository=goalRepository;
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
    public Task createTask(String name, Goal goal){
        User current = getCurrentUser();
        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole());

        if (!(isAdmin || goal.getHost().getId().equals(current.getId()) || goal.getMembers().contains(current))) {
            throw new AccessDeniedException("Not authorized to create task in this goal");
        }

        List<Task> tasks = taskRepository.findByGoal(goal);
        if(tasks.stream().anyMatch(t->t.getName().equals(name) && t.getGoal().getId().equals(goal.getId()))){
            throw new IllegalArgumentException("task already exists!");
        }
        Task task = new Task(name, goal);
        goal.setTotalPoints(goal.getTotalPoints()+task.getDifficulty().getWeight());
        goalRepository.save(goal);
        return taskRepository.save(task);
    }
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot delete non-existing task"));
        User current = getCurrentUser();
        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole());

        if (!(isAdmin || task.getGoal().getHost().getId().equals(current.getId()))) {
            throw new AccessDeniedException("Not authorized to delete this task");
        }
        Goal goal=task.getGoal();
        goal.setTotalPoints(goal.getTotalPoints()-task.getDifficulty().getWeight());
        goalRepository.save(goal);
        taskRepository.delete(task);
    }
    public Task updateTaskName(Long id, String name){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        User current = getCurrentUser();
        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole());

        if (!(isAdmin || task.getGoal().getHost().getId().equals(current.getId()))) {
            throw new AccessDeniedException("Not authorized to update this task");
        }
        task.setName(name);
        return taskRepository.save(task);
    }
    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(()->new IllegalArgumentException("no task with the given id"));
    }
    public List<Task> getTasksByGoalId(Long id){
        List<Task> tasks = taskRepository.findByGoal_Id(id);
        if(tasks.isEmpty()) throw new IllegalArgumentException("no task  associated to such a goal");
        return tasks;
    }
    //we need a way to add points to current user!
    public Task markDone(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        if(task.getTaskStatus() == TaskStatus.DONE) throw new IllegalCallerException("A fininshed task can't be remarked as done");
        task.setTaskStatus(TaskStatus.DONE);
        return taskRepository.save(task);
    }


}
