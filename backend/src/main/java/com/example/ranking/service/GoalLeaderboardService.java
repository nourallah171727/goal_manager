package com.example.ranking.service;

import com.example.goal.repo.GoalRepository;
import com.example.goal.entity.Goal;
import com.example.ranking.model.UserGoalScorePair;
import com.example.ranking.repo.UserScorePairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GoalLeaderboardService {
    private UserScorePairRepository userScorePairRepository;
    private GoalRepository goalRepository;
    @Autowired
    public GoalLeaderboardService(UserScorePairRepository userGoalScorePairRepo,GoalRepository goalRepository){
        this.userScorePairRepository=userGoalScorePairRepo;
        this.goalRepository=goalRepository;
    }


    public  List<UserGoalScorePair> topK(int k , Long goalId){
        Objects.requireNonNull(goalId);
        Optional<Goal> optionalGoal=goalRepository.findById(goalId);
        if(optionalGoal.isEmpty()){
            throw new IllegalArgumentException("goal not found");
        }
            return userScorePairRepository.findByGoalIdOrderByScoreDesc(goalId, PageRequest.of(0,k));
    }
}
