package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "goals")
public class Goal {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;
    @NotNull
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "stand")
    private GoalStand goalStand;

    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @ManyToOne
    @JoinColumn(name = "goal_user")
    private User user;
    @JsonIgnore
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Task> tasks=new HashSet<>();

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(name, goal.name) && goalStand == goal.goalStand && Objects.equals(dueDate, goal.dueDate) && Objects.equals(user, goal.user) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, goalStand, dueDate, user);
    }
}
