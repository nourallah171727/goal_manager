package com.example.task.controller;
import com.example.goal.entity.Goal;
import com.example.task.entity.Task;
import com.example.task.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE}, path = "/task")
public class TaskResource {
    private final TaskService service;
    public TaskResource(TaskService service) {
        this.service = service;
    }
    //any user
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable("taskId") Long taskId){
        try {
            Task task = service.getTaskById(taskId);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //any user
    @GetMapping("/all/{goalId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable("goalId") Long goalId){
        try{
            List<Task> list = service.getTasksByGoalId(goalId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //only host or admin
    @PostMapping("/{goalId}")
    public ResponseEntity<Task> createTask(@PathVariable Long goalId,Task task){
        try {
            Task createdTask = service.createTask(goalId, task);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //only host or admin
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTaskName(@PathVariable("taskId") Long taskId, @RequestParam String name){
        try{
            Task task = service.updateTaskName(taskId, name);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /*
    //only host or admin
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") Long taskId){
        try{
            service.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }*/
    @PostMapping("/{id}/done")
    public ResponseEntity<Void> markTaskAsDone(@PathVariable Long taskId) {
        service.markDone(taskId);
        return ResponseEntity.ok().build();
    }
}
