package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("no user with such ID"));
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public User createUser(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("name already used");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("email already used");
        }
        return repository.save(user);
    }

    public User updateUser(Long id, User user) {
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("user should already be in db");
        }
        if (repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("name already exists");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }

        user.setId(id);
        return repository.save(user);
    }

    public void deleteById(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("user must already be in the db");
        }
        repository.deleteById(id);
    }

    public void follow(Long followerId, Long followeeId) {
        // TODO: implement follow logic with entities
    }

    public void unfollow(User user) {
        // TODO: implement unfollow logic with entities
    }
}