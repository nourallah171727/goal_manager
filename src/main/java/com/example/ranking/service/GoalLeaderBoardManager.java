package com.example.ranking.service;

import com.example.goal.entity.Goal;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
//all of what this does is  storing goals with their ranking data structure for goals with >=1000 members
//an empty optional means the goal parameter has <1000 members , and hence the calling service
//would use a naive database query instead
@Service
public class GoalLeaderBoardManager {
    //stores goal_id -> its ranking data structure based on number of its members

    private ConcurrentHashMap<Long, RankingOfUsersPerGoal> cache=new ConcurrentHashMap<>();
    public Optional<RankingOfUsersPerGoal> getRanking(Goal goal){
        Objects.requireNonNull(goal);
        //only use data structure for big goals , otherwise , too much memory!
        if(goal.getMembers().size()>=1000){
           return Optional.of(cache.computeIfAbsent(goal.getId(),id->new RankingOfUsersPerGoal(goal)));
        }
        return Optional.empty();
    }
}
