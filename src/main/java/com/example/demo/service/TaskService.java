package com.example.demo.service;
import com.example.demo.model.Goal;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    private final GoalRepository goalRepository;
    private final TaskRepository repository;
    @Autowired
    public TaskService(TaskRepository repository, GoalRepository goalRepository){
        this.repository = repository;
        this.goalRepository = goalRepository;
    }
    public Task createTask(Task task, Long goalId){
        Optional<Goal> optGoal = goalRepository.findById(goalId);
        if (optGoal.isEmpty()){
            throw new IllegalArgumentException("the given goal ID must be valid");
        }
        Goal goal = optGoal.get();

        List<Task> tasks = repository.findByGoal(goal);
        if(tasks.stream().anyMatch(t->t.getName().equals(task.getName()))){
            throw new IllegalArgumentException("task already exists!");
        }
        task.setGoal(goal);
        return repository.save(task);
    }
    public void deleteTask(Long id){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot delete non existing tasks"));
        repository.delete(task);
    }
    public Task updateTaskName(Long id, String name){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        task.setName(name);
        return repository.save(task);
    }
    public Task getTaskById(Long id){
        return repository.findById(id).orElseThrow(()->new IllegalArgumentException("no task with the given id"));
    }
    public List<Task> getTasksByGoalId(Long id){
        List<Task> tasks = repository.findByGoal_Id(id);
        if(tasks.isEmpty()) throw new IllegalArgumentException("no task  associated to such a goal");
        return tasks;
    }
    public Task markDone(Long id){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        if(task.getTaskStatus() == TaskStatus.DONE) throw new IllegalCallerException("A fininshed task can't be remarked as done");
        task.setTaskStatus(TaskStatus.DONE);
        return repository.save(task);
    }
}
