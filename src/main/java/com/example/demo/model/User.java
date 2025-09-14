package com.example.demo.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Set<Goal> getGoals() {
        return goals;
    }

    public void setGoals(Set<Goal> goals) {
        this.goals = goals;
    }
    @Column(name="username")
    private String username;
    @Column(name="email")
    private String email;
    @Column(name="password")
    private String password;
    @ElementCollection(targetClass = GoalCategory.class)
    @CollectionTable(
            name = "user_categories",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING) // store enum name instead of ordinal
    @Column(name = "category")
    private Set<GoalCategory> categories = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "follows",                                     // join table
            joinColumns = @JoinColumn(name = "follower_id"),      // this user
            inverseJoinColumns = @JoinColumn(name = "followee_id") // the one being followed
    )
    private Set<User> following = new HashSet<>();

    // Users that follow this user
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Goal>goals;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", goals=" + goals +
                '}';
    }
}