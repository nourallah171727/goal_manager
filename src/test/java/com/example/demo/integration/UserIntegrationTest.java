package com.example.demo.integration;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        User user = new User("alice", "alice@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findById(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("alice");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
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