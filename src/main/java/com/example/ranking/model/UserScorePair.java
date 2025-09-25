package com.example.ranking.model;

import com.example.user.entity.User;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "goal_members")
@IdClass(UserScorePairId.class)
public class UserScorePair  implements Comparable<UserScorePair>{
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name="score",nullable = false)
    private Integer score;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUser(Long userId) {
        this.userId = userId;
    }

    public UserScorePair(Long userId, Long goalId){
        this.userId=userId;
        this.goalId=goalId;
    }
    public UserScorePair(){}
    public int compareTo(UserScorePair userScorePair){
        return score - userScorePair.score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserScorePair that = (UserScorePair) o;
        return Objects.equals(userId, that.userId) && Objects.equals(goalId, that.goalId) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, goalId, score);
    }
}