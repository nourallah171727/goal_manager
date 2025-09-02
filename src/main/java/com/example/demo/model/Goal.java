package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id", nullable = false, unique = true)
    private Long id;
    
    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "stand")
    private GoalStand goalStand;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @ManyToOne
    @JoinColumn(name = "goal_user", nullable = false)
    private User user;

    public Goal() {
        this.goalStand = GoalStand.NOT_STARTED;
        this.dueDate = null;
    }

    public Goal(String name, User user) {
        this();
        this.name = name;
        this.user = user;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoalStand getGoalStand() {
        return goalStand;
    }

    public void setGoalStand(GoalStand goalStand) {
        this.goalStand = goalStand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
