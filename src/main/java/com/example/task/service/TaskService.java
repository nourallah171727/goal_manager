package com.example.task.service;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.ranking.model.UserGoalScorePair;
import com.example.ranking.model.UserGoalScorePairId;
import com.example.ranking.repo.UserScorePairRepository;
import com.example.task.entity.Task;
import com.example.user.entity.User;
import com.example.task.repo.TaskRepository;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final UserScorePairRepository userScorePairRepository;
    @Autowired
    public TaskService(TaskRepository repository,UserRepository userRepository,GoalRepository goalRepository,UserScorePairRepository userScorePairRepository){
        this.taskRepository = repository;
        this.userRepository=userRepository;
        this.goalRepository=goalRepository;
        this.userScorePairRepository=userScorePairRepository;
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
    public Task createTask(Long goalId, Task task) {
        User current = getCurrentUser();
        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole());

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (!(isAdmin || goal.getHost().getId().equals(current.getId()))) {
            throw new AccessDeniedException("Not authorized to create task in this goal");
        }
        if(goal.getDueDate()!=null && LocalDate.now().isAfter(goal.getDueDate())){
            throw new IllegalStateException("can't add a task for an expired goal");
        }

        List<Task> tasks = taskRepository.findByGoal(goal);
        if (tasks.stream().anyMatch(t -> t.getName().equals(task.getName()))) {
            throw new IllegalArgumentException("Task with the same name already exists in this goal!");
        }
        task.setGoal(goal);
        goalRepository.incrementTotalPoints(goalId,task.getDifficulty().getWeight());

        return taskRepository.save(task);
    }
    /*
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
    }*/
    public Task updateTaskName(Long id, String name){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        User current = getCurrentUser();
        boolean isAdmin = "ROLE_ADMIN".equals(current.getRole());

        if (!(isAdmin || task.getGoal().getHost().getId().equals(current.getId()))) {
            throw new AccessDeniedException("Not authorized to update this task");
        }
        if(task.getGoal().getDueDate()!=null && LocalDate.now().isAfter(task.getGoal().getDueDate())){
            throw new IllegalStateException("can't add a task for an expired goal");
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

    public void markDone(Long taskId,Long userId){

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("cannot update non existing tasks"));
        if(task.getGoal().getDueDate()!=null && LocalDate.now().isAfter(task.getGoal().getDueDate())){
            throw new IllegalStateException("can't mark a task as done for an expired goal");
        }
        User user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));

        if(!user.getGoals().contains(task.getGoal())){
            throw new AccessDeniedException("you are not a member of the goal!");
        }

        if(user.getFinishedTasks().contains(task)){
            throw new IllegalArgumentException("A finished task can't be remarked as done");
        }

        userRepository.insertFinishedTask(userId,taskId);

       userScorePairRepository.incrementScore(task.getGoal().getId(),userId,task.getDifficulty().getWeight());
   }


}
