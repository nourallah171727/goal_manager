package com.example.demo.controller;

import com.example.demo.model.Goal;
import com.example.demo.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goal")
public class GoalResource {
    private final GoalService goalService;

    @Autowired
    public GoalResource(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoal(@PathVariable("goalId") Long goalId) {
        try {
            Goal goal = goalService.getGoalById(goalId);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Goal>> getAllGoals() {
        return ResponseEntity.ok(goalService.getGoals());
    }



    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable("id") Long id, @RequestBody Goal goalDetails) {
        try {
            return ResponseEntity.ok(goalService.updateGoal(id, goalDetails));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("id") Long id) {
        try {
            goalService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}