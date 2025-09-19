package com.example.ranking.userPerGoal;

import com.example.model.Goal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalLeaderboardService {
    private GoalLeaderBoardManager manager;

    public GoalLeaderBoardManager getManager() {
        return manager;
    }

    public void setManager(GoalLeaderBoardManager manager) {
        this.manager = manager;
    }

    public GoalLeaderboardService(GoalLeaderBoardManager manager) {
        this.manager = manager;
    }
    public  List<UserScorePair> topK(int k , Goal goal){
        Optional<RankingOfUsersPerGoal> ranking=manager.getRanking(goal);
        if(ranking.isEmpty()){
            //do a naive database query
            return null;
        }else{
            return ranking.get().topK(k);
        }
    }
}
