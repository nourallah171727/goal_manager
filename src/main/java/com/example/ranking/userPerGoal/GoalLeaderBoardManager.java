package com.example.ranking.userPerGoal;

import com.example.model.Goal;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GoalLeaderBoardManager {
    //stores goal_id -> its ranking data structure based on number of its members
    ConcurrentHashMap<Long,RankingOfUsersPerGoal> cache=new ConcurrentHashMap<>();
    public Optional<RankingOfUsersPerGoal> getRanking(Goal goal){
        Objects.requireNonNull(goal);
        //only use data structure for big goals , otherwise , too much memory!
        if(goal.getMembers().size()>=1000){
           return Optional.of(cache.computeIfAbsent(goal.getId(),id->new RankingOfUsersPerGoal(goal)));
        }
        return Optional.empty();
    }
}
