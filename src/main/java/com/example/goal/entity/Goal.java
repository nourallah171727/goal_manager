package com.example.goal.entity;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalStand;
import com.example.goal.common.GoalType;
import com.example.task.entity.Task;
import com.example.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
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
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private GoalCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private GoalType type = GoalType.PUBLIC;

    @Column(name = "private_code")
    private String privateCode;

    @Column(name = "votes_to_mark_completed")
    private int votesToMarkCompleted = 1;
    @Column(name="totalgoalpoints")
    private int totalPoints;

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    @ManyToOne
    @JoinColumn(name = "goal_host")
    private User host;  // host

    @JsonIgnore
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "goal_members",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private Set<User> members = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "goal_stars",
            joinColumns = @JoinColumn(name = "goal_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> starredBy = new HashSet<>();


    public Goal() {
    }

    public Goal(String name, User host) {
        this();
        this.name = name;
        this.host = host;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
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



       public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public GoalCategory getCategory() {
        return category;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalType getType() {
        return type;
    }

    public void setType(GoalType type) {
        this.type = type;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public int getVotesToMarkCompleted() {
        return votesToMarkCompleted;
    }

    public void setVotesToMarkCompleted(int votesToMarkCompleted) {
        this.votesToMarkCompleted = votesToMarkCompleted;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<User> getStarredBy() {
        return starredBy;
    }

    public void setStarredBy(Set<User> starredBy) {
        this.starredBy = starredBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id)  ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
