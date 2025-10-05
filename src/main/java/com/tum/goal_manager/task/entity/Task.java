package com.tum.goal_manager.task.entity;
import com.tum.goal_manager.goal.entity.Goal;
import com.tum.goal_manager.task.common.TaskDifficulty;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    public Task() {
    }

    public Task(String name, Goal goal) {
        this();
        this.name = name;
        this.goal = goal;
    }

    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name="difficulty")
    TaskDifficulty difficulty;
    @ManyToOne
    @JoinColumn(name = "task_goal")
    private Goal goal;

    public Long getId() {
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



    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
    public TaskDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(TaskDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
