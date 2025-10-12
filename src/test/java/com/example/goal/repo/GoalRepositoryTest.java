package com.example.goal.repo;

import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.goal.entity.Goal;
import com.example.goal.common.GoalStand;
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
class GoalRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindGoal() {
        User user=new User("ahmed","ahmed@gmail.com","some password");
        user.setRole("USER");
        Goal goal=new Goal("name",user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        userRepository.save(user);
        goalRepository.save(goal);
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>found=goalRepository.findById(goal.getId());
        Optional<User>newUser=userRepository.findById(user.getId());
        Assertions.assertTrue((found).isPresent(),"found is not present");
        Assertions.assertEquals(found.get(),goal,"found and goal are not equal");
        Assertions.assertTrue(newUser.isPresent(),"user not present");
        Assertions.assertEquals(found.get().getHost(),newUser.get());
        Assertions.assertFalse(newUser.get().getHostedGoals().isEmpty(),"user's goals are empty");
        Assertions.assertTrue(newUser.get().getHostedGoals().contains(found.get()),"user does not have the saved goal");
    }
    @Test
    void testMembersOfGoal(){
        User user=new User("ahmed","ahmed@gmail.com","some password");
        user.setRole("USER");
        Goal goal=new Goal("name",user);
        goal.setCategory(GoalCategory.SPORTS);
        goal.setType(GoalType.PUBLIC);
        userRepository.save(user);
        goalRepository.save(goal);
        entityManager.flush();
        entityManager.clear();
        goal.getMembers().add(user);
        goalRepository.save(goal);
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>DBGoal=goalRepository.findById(goal.getId());
        Optional<User>DBuser=userRepository.findById(user.getId());
        Assertions.assertTrue((DBGoal).isPresent(),"DBGoal not present");
        Assertions.assertEquals(DBGoal.get(),goal,"found and goal are not equal");
        Assertions.assertTrue(DBuser.isPresent(),"user not present");
        Assertions.assertFalse(DBGoal.get().getMembers().isEmpty(),"a");
        Assertions.assertFalse(DBuser.get().getGoals().isEmpty(),"b");
        Assertions.assertTrue(DBGoal.get().getMembers().contains(DBuser.get()),"user does not have the saved goal");
        Assertions.assertTrue(DBuser.get().getGoals().contains(DBGoal.get()),"user does not have the saved goal");


    }
    /*
    @Test
    void testUpdateGoal(){
        User user=new User("ahmed","ahmed@gmail.com");
        Goal goal=new Goal("name",user);
        userRepository.save(user);
        goalRepository.save(goal);
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>found=goalRepository.findById(goal.getId());
        assertThat(found).isPresent();
        found.get().setName("pipoupa");
        goalRepository.save(found.get());
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>newGoal=goalRepository.findById(found.get().getId());
        Optional<User>newUser=userRepository.findById(user.getId());
        Assertions.assertTrue(newGoal.isPresent(),"newGoal should be present");
        Assertions.assertEquals(newGoal.get(),found.get());
        Assertions.assertTrue(newUser.get().getGoals().contains(newGoal.get()));
    }
    @Test
    void testDeleteGoal(){
        User user =new User("nourallah","nourallah@gmail.com");
        Goal goal=new Goal("name",user);
        userRepository.save(user);
        goalRepository.save(goal);
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>found =goalRepository.findById(goal.getId());
        Assertions.assertTrue(found.isPresent(),"found is not present");
        goalRepository.deleteById(found.get().getId());
        entityManager.flush();
        entityManager.clear();
        Optional<Goal>newGoal=goalRepository.findById(goal.getId());
        Assertions.assertTrue(newGoal.isEmpty(),"new goal is not empty");
        Optional<User>newUser=userRepository.findById(user.getId());
       Assertions.assertTrue(newUser.get().getGoals().isEmpty(),"new goal is not deleted from the user's set ");
    }
    */

}