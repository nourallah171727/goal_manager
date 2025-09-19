package com.example.ranking.userPerGoal;

import com.example.model.User;
import com.example.ranking.common.Pair;

public class UserScorePair extends Pair<User,Integer> implements Comparable<UserScorePair>{
    public UserScorePair(User user,Integer score){
        super(user,score);
    }
    public int compareTo(UserScorePair userScorePair){
        return getB() - userScorePair.getB();
    }

    @Override
    public boolean equals(Object obj) {
       if(obj==null){
           return false;
       }
       if(!(obj instanceof UserScorePair casted)){
           return false;
       }
       return casted.getA().getId().equals(getA().getId());
    }
}