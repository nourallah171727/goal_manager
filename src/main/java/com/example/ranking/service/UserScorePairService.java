package com.example.ranking.service;

import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.ranking.model.UserGoalScorePair;
import com.example.ranking.model.UserGoalScorePairId;
import com.example.ranking.repo.UserScorePairRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserScorePairService {
    private final UserScorePairRepository userScorePairRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    @Autowired

    public UserScorePairService(UserScorePairRepository userScorePairRepository, UserRepository userRepository,GoalRepository goalRepository) {
        this.userScorePairRepository = userScorePairRepository;
        this.userRepository=userRepository;
        this.goalRepository=goalRepository;
    }
    public void joinGoal(Long userId,Long goalId){
        User user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));
        Goal goal=goalRepository.findById(goalId).orElseThrow(()->new IllegalArgumentException("goal not found"));
        if(userScorePairRepository.existsByUserIdAndGoalId(userId,goalId)){
            throw new IllegalArgumentException("already joined in");
        }
        UserGoalScorePair userGoalScorePair=new UserGoalScorePair(userId,goalId,0);
        userScorePairRepository.save(userGoalScorePair);
    }
    public void leaveGoal(Long userId, Long goalId){
        User user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));
        Goal goal=goalRepository.findById(goalId).orElseThrow(()->new IllegalArgumentException("goal not found"));
        if(!userScorePairRepository.existsByUserIdAndGoalId(userId,goalId)){
            throw new IllegalArgumentException("user is already not joined in!");
        }
        userScorePairRepository.deleteById(new UserGoalScorePairId(userId,goalId));


    }
}
