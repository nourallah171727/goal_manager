package com.tum.goal_manager.upload.entity;

import java.io.Serializable;
import java.util.Objects;

public class UploadId implements Serializable {
    private Long userId;
    private Long taskId;

    public UploadId() {}

    public UploadId(Long userId, Long taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    // Getters & Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, taskId);
    }
}