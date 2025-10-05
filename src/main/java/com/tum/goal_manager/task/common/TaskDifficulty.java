package com.tum.goal_manager.task.common;

public enum TaskDifficulty {
    EASY(1),
    MEDIUM(5),
    DIFFICULT(10);

    private final int weight;

    TaskDifficulty(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}