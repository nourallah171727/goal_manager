package com.example.user;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Test
    void testSaveAndFindUser() {
        String rawPassword="some password";
        User user = new User("alice", "alice@example.com",rawPassword);
        user.setRole("USER");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User> found = userRepository.findById(user.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertNotEquals(rawPassword,found.get().getPassword());
        Assertions.assertEquals(found.get().getUsername(),user.getUsername());
        Assertions.assertEquals(found.get().getId(),user.getId());

    }
    @Test
    void testUpdateUser(){
        User user=new User("firas ben hmiden","firasBenHmiden@gmail.com","some password");
        user.setRole("USER");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        String hashedPass=user.getPassword();
        user.setPassword("some other password");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User> found=userRepository.findById(user.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(found.get(),user);
        Assertions.assertNotEquals(found.get().getPassword(),"some other password");
        Assertions.assertNotEquals(hashedPass,found.get().getPassword());
    }
    @Test
    void testDeleteUser(){
        User user =new User("nourallah","nourallah@gmail.com","some password");
        user.setRole("USER");
        Goal goal=new Goal("name",user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        userRepository.save(user);
        goalRepository.save(goal);

        entityManager.flush();
        entityManager.clear();
        Optional<User>found =userRepository.findById(user.getId());
        Assertions.assertTrue(found.isPresent());
        userRepository.deleteById(user.getId());
        entityManager.flush();
        entityManager.clear();
        Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty());
        Assertions.assertTrue(goalRepository.findById(goal.getId()).isEmpty());
    }
    @Test
    void followUser(){
        User follower =new User("nourallah","nourallah@gmail.com","some password");
        follower.setRole("USER");
        User followee =new User("anotherNourallah","anotherNourallah@gmail.com","some password");
        followee.setRole("USER");
        userRepository.save(follower);
        userRepository.save(followee);
        follower.getFollowing().add(followee);
        userRepository.save(follower);
        entityManager.flush();
        entityManager.clear();
        Optional<User> followerResult=userRepository.findById(follower.getId());
        Assertions.assertFalse(followerResult.isEmpty());
        Optional<User> followeeResult=userRepository.findById(followee.getId());
        Assertions.assertFalse(followeeResult.isEmpty());
        Assertions.assertTrue(followerResult.get().getFollowing().contains(followeeResult.get()));
        Assertions.assertTrue(followeeResult.get().getFollowers().contains(followerResult.get()));
    }
    @Test
    void unfollowUser(){
        User follower =new User("nourallah","nourallah@gmail.com","some password");
        follower.setRole("USER");
        User followee =new User("anotherNourallah","anotherNourallah@gmail.com","some password");
        followee.setRole("USER");
        userRepository.save(follower);
        userRepository.save(followee);
        follower.getFollowing().add(followee);
        userRepository.save(follower);
        entityManager.flush();
        entityManager.clear();
        Optional<User> followerResult=userRepository.findById(follower.getId());
        Assertions.assertFalse(followerResult.isEmpty());
        followerResult.get().getFollowing().clear();
        userRepository.save(followerResult.get());
        entityManager.flush();
        entityManager.clear();
        Optional<User> newFollowerResult=userRepository.findById(follower.getId());
        Optional<User> followeeResult=userRepository.findById(followee.getId());

        Assertions.assertTrue(newFollowerResult.get().getFollowing().isEmpty());
        Assertions.assertTrue(followeeResult.get().getFollowers().isEmpty());
    }
}