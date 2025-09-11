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
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;


    @Autowired
    public GoalService(GoalRepository goalRepository,UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository=userRepository;
    }

    public Goal getGoalById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("goalId is null");
        }
        return goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("no goal with such ID"));
    }

    public List<Goal> getGoals() {
        return goalRepository.findAll();
    }

    public Goal createGoal(Goal goal,Long userId) {
        if(userId==null){
            throw new IllegalArgumentException("userId should not be null");
        }
        Optional<User> optUser=userRepository.findById(userId);
        if(optUser.isEmpty()){
            throw new IllegalArgumentException("userId should be a valid Id");
        }
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goal.getId() != null) {
            throw new IllegalArgumentException("goal should not already have an ID!");
        }
        goal.setUser(optUser.get());
        return goalRepository.save(goal);
    }

    public Goal updateGoal(Long id, Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goalRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("goal must already be in the db");
        }
        if(goal.getId()!=null){
            throw new IllegalArgumentException("goal object shall not contain an id , you must update only through path variable");
        }
        goal.setId(id);

        return goalRepository.save(goal);
    }

    public void deleteById(Long id) {
        if (id == null || goalRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("goal must already be in the db");
        }
        goalRepository.deleteById(id);
    }
}