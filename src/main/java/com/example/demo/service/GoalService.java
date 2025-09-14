package com.example.demo.service;

import com.example.demo.model.Goal;
import com.example.demo.model.GoalType;
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
        if(id==null){
            throw new IllegalArgumentException("id must not be null");
        }
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

    private User validateAndGetUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("userId must be valid"));
    }
    public void joinGoal(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getMembers().add(user);
        goalRepository.save(goal);
    }
    public void leaveGoal(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getMembers().remove(user);
        goalRepository.save(goal);
    }

    public void addStar(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getStarredBy().add(user);
        goalRepository.save(goal);
    }

    public void removeStar(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getStarredBy().remove(user);
        goalRepository.save(goal);
    }

    public List<Goal> findGoalsByCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        return goalRepository.findByCategory(category);
    }

    public List<Goal> findGoalsByType(GoalType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        return goalRepository.findByType(type);
    }

    public List<Goal> findGoalsByHost(Long hostId) {
        if (hostId == null) {
            throw new IllegalArgumentException("hostId must not be null");
        }
        Optional<User> optHost = userRepository.findById(hostId);
        if (optHost.isEmpty()) {
            throw new IllegalArgumentException("hostId must be valid");
        }
        return goalRepository.findByHost(optHost.get());
    }

    public List<Goal> findGoalsByMember(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("userId must be valid");
        }
        return goalRepository.findByMembersContaining(optUser.get());
    }
}