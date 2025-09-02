package com.example.demo.controller;
import com.example.demo.model.Goal;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
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
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable("taskId") Long taskId){
        try {
            Task task = service.getTaskById(taskId);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") Long taskId){
        try{
            service.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/all/{goalId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable("goalId") Long goalId){
        try{
            List<Task> list = service.getTasksByGoalId(goalId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestParam("name") String name, @RequestBody Goal goal){
        try {
            Task task = service.createTask(name, goal);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTaskName(@PathVariable("taskId") Long taskId, @RequestParam String name){
        try{
            Task task = service.updateTaskName(taskId, name);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
