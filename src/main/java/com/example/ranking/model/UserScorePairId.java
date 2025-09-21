package com.example.ranking.model;

import java.io.Serializable;
import java.util.Objects;

public class UserScorePairId implements Serializable {
    private Long user;   // refers to user.id
    private Long goalId;

    public UserScorePairId() {}

    public UserScorePairId(Long user, Long goalId) {
        this.user = user;
        this.goalId = goalId;
    }

    // Getters and setters
    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
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
        return Objects.equals(user, that.user) &&
                Objects.equals(goalId, that.goalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, goalId);
    }
}