package com.tum.goal_manager.user.service;

import com.tum.goal_manager.user.entity.User;
import com.tum.goal_manager.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    @Autowired
    public UserService(UserRepository repo) {
        this.repository = repo;
    }
    public User getUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("no user with such ID"));
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public void validateUniqueEmailAndUsrName(User user){
        Optional<User> userOptional=repository.findByUsernameOrEmail(user.getUsername(),user.getEmail());
        if(userOptional.isPresent()){
            throw new IllegalArgumentException("user already exists");
        }
    }


    public User createUser(User user) {
        validateUniqueEmailAndUsrName(user);
        return repository.save(user);
    }

    public User updateUser(Long id, User user) {
        //check if the sender of update request is actually the same user , or a random admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        User currentUser = repository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUser.getId().equals(id) && !isAdmin) {
            throw new AccessDeniedException("You cannot update this user");
        }
        //other checks
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("user should already be in db");
        }
        validateUniqueEmailAndUsrName(user);
        //for actual update
        user.setId(id);
        return repository.save(user);
    }

    public void deleteById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        User currentUser = repository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUser.getId().equals(id) && !isAdmin ) {
            throw new AccessDeniedException("You cannot delete this user");
        }
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("user must already be in the db");
        }
        repository.deleteById(id);
    }

    public void follow(Long followerId, Long followeeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        User currentUser = repository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!currentUser.getId().equals(followerId) ) {
            throw new AccessDeniedException("You cannot delete this user");
        }
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        User currentUser = repository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!currentUser.getId().equals(followerId) ) {
            throw new AccessDeniedException("You cannot delete this user");
        }
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