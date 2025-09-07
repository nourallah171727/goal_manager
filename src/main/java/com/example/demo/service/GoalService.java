package com.example.demo.service;

import com.example.demo.model.Goal;
import com.example.demo.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {
    private final GoalRepository repository;

    @Autowired
    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    public Goal getGoalById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("goalId is null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("no goal with such ID"));
    }

    public List<Goal> getGoals() {
        return repository.findAll();
    }

    public Goal createGoal(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goal.getId() != null) {
            throw new IllegalArgumentException("goal should not already have an ID!");
        }
        return repository.save(goal);
    }

    public Goal updateGoal(Long id, Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goal.getId() == null || repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("goal must already be in the db");
        }
        return repository.save(goal);
    }

    public void deleteById(Long id) {
        if (id == null || repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("goal must already be in the db");
        }
        repository.deleteById(id);
    }
}