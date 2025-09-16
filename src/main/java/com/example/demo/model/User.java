package com.example.demo.model;
import jakarta.persistence.*;

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
    @Column(name="encodedPassword")
    private String encodedPassword;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

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

    @ManyToMany(mappedBy = "members")
    private Set<Goal>goals;

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public Set<GoalCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<GoalCategory> categories) {
        this.categories = categories;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public User() {
    }

    public User(String username, String email,String encodedPassword) {
        this.username = username;
        this.email = email;
        this.encodedPassword=encodedPassword;
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