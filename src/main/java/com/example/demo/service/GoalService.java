package com.example.demo.service;

import com.example.demo.model.Goal;
import com.example.demo.model.User;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {
    private final UserRepository userRepository;
    private final GoalRepository repository;

    @Autowired
    public GoalService(GoalRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

    public Goal createGoal(Goal goal, Long userId) {
        if(userId == null){
            throw new IllegalArgumentException("userId should not be null");
        }
        Optional<User> optUser = userRepository.findById(userId);
        if(optUser.isEmpty()){
            throw new IllegalArgumentException("userId should be a valid Id");
        }
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goal.getId() != null) {
            throw new IllegalArgumentException("goal should not have an ID yet!");
        }
        goal.setUser(optUser.get());
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