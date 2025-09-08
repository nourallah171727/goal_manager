package com.example.demo.integration;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        User user = new User("alice", "alice@example.com");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User> found = userRepository.findById(user.getId());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(found.get(),user);
    }
    @Test
    void testUpdateUser(){
        User user=new User("firas ben hmiden","firasBenHmiden@gmail.com");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        user.setEmail("anothermail@gmail.com");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User> found=userRepository.findById(user.getId());
        Assertions.assertTrue(found.isPresent());
       Assertions.assertEquals(found.get(),user);
    }
    @Test
    void testDeleteUser(){
        User user =new User("nourallah","nourallah@gmail.com");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        Optional<User>found =userRepository.findById(user.getId());
        Assertions.assertTrue(found.isPresent());
        userRepository.deleteById(user.getId());
        entityManager.flush();
        entityManager.clear();
        Assertions.assertTrue(userRepository.findById(user.getId()).isEmpty());
    }
}