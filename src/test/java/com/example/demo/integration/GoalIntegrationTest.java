package com.example.demo.integration;

import com.example.demo.model.Goal;
import com.example.demo.model.GoalStand;
import com.example.demo.model.User;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindGoal() {
        User user=new User("ahmed","ahmed@gmail.com");
        Goal goal=new Goal("name",user);
        Assertions.assertEquals(goal.getGoalStand(), GoalStand.NOT_STARTED);
        userRepository.save(user);
        goalRepository.save(goal);
        Optional<Goal>found=goalRepository.findById(goal.getId());
        assertThat(found).isPresent();
        assertThat(found.get().equals(goal));
        assertThat(user.getGoals().contains(found.get()));
    }
    @Test
    void testUpdateUser(){
        User user=new User("firas ben hmiden","firasBenHmiden@gmail.com");
        userRepository.save(user);
        user.setEmail("anothermail@gmail.com");
        Optional<User> found=userRepository.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("firas ben hmiden");
        assertThat(found.get().getEmail()).isEqualTo("anothermail@gmail.com");
    }
    @Test
    void testDeleteUser(){
        User user =new User("nourallah","nourallah@gmail.com");
        userRepository.save(user);
        Optional<User>found =userRepository.findById(user.getId());
        assertThat(found).isPresent();
        userRepository.deleteById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}