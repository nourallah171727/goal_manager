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

import static org.assertj.core.api.Assertions.as;
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
        Assertions.assertTrue((found).isPresent(),"found is not present");
        Assertions.assertEquals(found.get(),goal,"found and goal are not equal");
        Assertions.assertFalse(user.getGoals().isEmpty(),"user's goals are empty");
        Assertions.assertTrue(user.getGoals().contains(found.get()),"user does not have the saved goal");
    }
    @Test
    void testUpdateGoal(){
        User user=new User("ahmed","ahmed@gmail.com");
        Goal goal=new Goal("name",user);
        Assertions.assertEquals(goal.getGoalStand(), GoalStand.NOT_STARTED);
        userRepository.save(user);
        goalRepository.save(goal);
        Optional<Goal>found=goalRepository.findById(goal.getId());
        assertThat(found).isPresent();
        found.get().setName("pipoupa");
        goalRepository.save(found.get());
        Optional<Goal>newGoal=goalRepository.findById(found.get().getId());
        Assertions.assertEquals(newGoal.get(),found.get());
        Assertions.assertTrue(user.getGoals().contains(newGoal));
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