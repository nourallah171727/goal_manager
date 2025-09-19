package com.example.ranking.model;

import com.example.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "goal_members")
@IdClass(UserScorePairId.class)
public class UserScorePair  implements Comparable<UserScorePair>{
    @Id
    //check lazy type fetching for later
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Id
    @Column(name = "goal_id", nullable = false)
    private Long goalId;
    @Column(name="score",nullable = false)
    private Integer score;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserScorePair(User user, Integer score){
        this.user=user;
        this.score=score;
    }
    public int compareTo(UserScorePair userScorePair){
        return score - userScorePair.score;
    }

    @Override
    public boolean equals(Object obj) {
       if(obj==null){
           return false;
       }
       if(!(obj instanceof UserScorePair casted)){
           return false;
       }
       return casted.user.getId().equals(user.getId());
    }
}