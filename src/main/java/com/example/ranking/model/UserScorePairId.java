package com.example.ranking.model;

import java.io.Serializable;
import java.util.Objects;

public class UserScorePairId implements Serializable {
    private Long userId;   // refers to user.id
    private Long goalId;

    public UserScorePairId() {}

    public UserScorePairId(Long userId, Long goalId) {
        this.userId = userId;
        this.goalId = goalId;
    }

    // Getters and setters
    public Long getUser() {
        return userId;
    }

    public void setUser(Long user) {
        this.userId = user;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserScorePairId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(goalId, that.goalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, goalId);
    }
}