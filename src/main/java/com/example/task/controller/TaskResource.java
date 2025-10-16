package com.example.task.controller;
import com.example.dto.DTOMapper;
import com.example.dto.task.TaskCreateDTO;
import com.example.dto.task.TaskResponseDTO;
import com.example.goal.entity.Goal;
import com.example.task.entity.Task;
import com.example.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/task")
public class TaskResource {
    private final TaskService service;
    private final DTOMapper dtoMapper;
    public TaskResource(TaskService service,DTOMapper dtoMapper) {
        this.service = service;
        this.dtoMapper=dtoMapper;
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
    public ResponseEntity<TaskResponseDTO> createTask(@PathVariable Long goalId,@RequestBody @Valid TaskCreateDTO task){
            Task createdTask = service.createTask(goalId, dtoMapper.createDtoToTask(task));
            return ResponseEntity.ok(dtoMapper.taskToResponseDTO(createdTask));
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
}
