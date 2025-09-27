package com.example.ranking.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "goal_members")
@IdClass(UserGoalScorePairId.class)
public class UserGoalScorePair implements Comparable<UserGoalScorePair>{
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "goal_id")
    private Long goalId;

    public UserGoalScorePair(Long userId, Long goalId, Integer score) {
        this.userId = userId;
        this.goalId = goalId;
        this.score = score;
    }

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

    public UserGoalScorePair(Long userId, Long goalId){
        this.userId=userId;
        this.goalId=goalId;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserGoalScorePair(){}
    public int compareTo(UserGoalScorePair userGoalScorePair){
        return score - userGoalScorePair.score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGoalScorePair that = (UserGoalScorePair) o;
        return Objects.equals(userId, that.userId) && Objects.equals(goalId, that.goalId) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, goalId, score);
    }
}