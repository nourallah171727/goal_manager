package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repository = repo;
        this.passwordEncoder = encoder;
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
        String encoded = passwordEncoder.encode(user.getEncodedPassword());
        user.setEncodedPassword(encoded);
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
        Optional<User>follower=repository.findById(followerId);
        Optional<User>followee=repository.findById(followeeId);
        if(followee.isEmpty() || follower.isEmpty()){
            throw new IllegalArgumentException("either follower or followeee doesn't exist");
        }
        if(follower.get().getId().equals(followee.get().getId())){
            throw new IllegalArgumentException("can't follow yourself");
        }
        follower.get().getFollowing().add(followee.get());
        followee.get().getFollowers().add(follower.get());
        repository.save(follower.get());
    }

    public void unfollow(Long followerId, Long followeeId) {
        // TODO: implement unfollow logic with entities
        Optional<User>follower=repository.findById(followerId);
        Optional<User>followee=repository.findById(followeeId);
        if(followee.isEmpty() || follower.isEmpty()){
            throw new IllegalArgumentException("either follower or followeee doesn't exist");
        }
        if(follower.get().getId().equals(followee.get().getId())){
            throw new IllegalArgumentException("can't unfollow yourself");
        }
        follower.get().getFollowing().remove(followee.get());
        followee.get().getFollowers().remove(follower.get());
        repository.save(follower.get());
    }
}