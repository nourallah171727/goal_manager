package com.tum.goal_manager.ranking.service;

import com.tum.goal_manager.goal.repo.GoalRepository;
import com.tum.goal_manager.goal.entity.Goal;
import com.tum.goal_manager.ranking.model.UserGoalScorePair;
import com.tum.goal_manager.ranking.repo.UserScorePairRepository;
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
