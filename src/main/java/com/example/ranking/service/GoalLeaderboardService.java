package com.example.ranking.service;

import com.example.goal.repo.GoalRepository;
import com.example.goal.entity.Goal;
import com.example.ranking.model.UserScorePair;
import com.example.ranking.repo.UserScorePairRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GoalLeaderboardService {
    private GoalLeaderBoardManager manager;
    private UserScorePairRepository userScorePairRepository;
    private GoalRepository goalRepository;

    public GoalLeaderBoardManager getManager() {
        return manager;
    }

    public void setManager(GoalLeaderBoardManager manager) {
        this.manager = manager;
    }

    public GoalLeaderboardService(GoalLeaderBoardManager manager) {
        this.manager = manager;
    }
    public  List<UserScorePair> topK(int k , Long goalId){
        Objects.requireNonNull(goalId);
        Optional<Goal> optionalGoal=goalRepository.findById(goalId);
        if(optionalGoal.isEmpty()){
            throw new IllegalArgumentException("goal not found");
        }
        Optional<RankingOfUsersPerGoal> ranking=manager.getRanking(optionalGoal.get());
        if(ranking.isEmpty()){
            //do a naive database query
            return userScorePairRepository.findByGoalIdOrderByScoreDesc(goalId, PageRequest.of(0,k));
        }else{
            return ranking.get().topK(k);
        }
    }
}
