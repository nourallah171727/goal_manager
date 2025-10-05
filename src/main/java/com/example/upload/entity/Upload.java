package com.example.upload.entity;


import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "uploads")
@IdClass(UploadId.class)
public class Upload {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "current_votes")
    private Integer currentVotes = 0;

    public Upload() {}

    public Upload(Long userId, Long taskId, String filePath, Integer currentVotes) {
        this.userId = userId;
        this.taskId = taskId;
        this.filePath = filePath;
        this.currentVotes = currentVotes;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(Integer currentVotes) {
        this.currentVotes = currentVotes;
    }

    // Equality on composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Upload upload)) return false;
        return Objects.equals(userId, upload.userId) &&
                Objects.equals(taskId, upload.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, taskId);
    }
}