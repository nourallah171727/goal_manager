package com.example.demo.model;
import jakarta.persistence.*;
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false, unique = true)
    private Long id;

    public Task() {
        this.taskStatus = TaskStatus.NOT_DONE;
    }

    public Task(String name, Goal goal) {
        this();
        this.name = name;
        this.goal = goal;
    }

    @Column(name = "name", length = 20)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;
    
    @ManyToOne
    @JoinColumn(name = "task_goal", nullable = false)
    private Goal goal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
