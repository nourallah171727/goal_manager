package com.example.ranking.userPerGoal;

import com.example.model.Goal;
import com.example.ranking.common.Ranking;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
//T : pair of User , Long stores (user , score) per goal
//K: user_id which we will use as key of data structure

public class RankingOfUsersPerGoal implements Ranking<UserScorePair,Long> {
    private Goal goal;
    private PriorityBlockingQueue<UserScorePair> priorityQueue;
    private ConcurrentHashMap<Long , UserScorePair> map;

    public Goal getGoal() {
        return goal;
    }
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public RankingOfUsersPerGoal(Goal goal) {
        this.goal = goal;
        //need priorityQueue to be a max heap , cuz per default it is a min heap
        priorityQueue=new PriorityBlockingQueue<>(goal.getMembers().size(),Comparator.reverseOrder());
        map=new ConcurrentHashMap<>();

    }
    //O(k log(n)) for small k -> O(log(n))
    public List<UserScorePair> topK(int k){
        List<UserScorePair> list=new ArrayList<>(k);
        for(int i=0 ; i<k;i++){
            //O(log(n))
            UserScorePair pair=priorityQueue.poll();
            Objects.requireNonNull(pair);
            list.add(pair);
            map.remove(pair.getA().getId(),pair);

        }
        return list;
    }
    //O(log(n))
    public void add(UserScorePair toAdd){
        Long userId=toAdd.getA().getId();
        priorityQueue.add(toAdd);
        map.put(toAdd.getA().getId(),toAdd);
    }
    //O(n)
    public void update(Long userId,UserScorePair newElement){
        UserScorePair old=map.get(userId);
        priorityQueue.remove(old);
        priorityQueue.add(newElement);
        map.put(userId,newElement);
    }
    //O(n)
    public void remove(Long userId){
        UserScorePair old=map.get(userId);
        priorityQueue.remove(old);
        map.remove(userId,old);
    }
}
