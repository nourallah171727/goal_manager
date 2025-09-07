package com.example.demo.service;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    // --- getUserById ---
    @Test
    void getUserByIdNull() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(null)
        );
        Assertions.assertEquals("userId is null", ex.getMessage());
    }

    @Test
    void getUserByIdNotExisting() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(42L)
        );
        Assertions.assertEquals("no user with such ID", ex.getMessage());
    }

    @Test
    void getUserByIdExisting() {
        User user = new User("name", "email");
        user.setId(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user, userService.getUserById(42L));
    }

    // --- getUsers ---
    @Test
    void getUsersReturnsAll() {
        User u1 = new User("n1", "e1");
        User u2 = new User("n2", "e2");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.getUsers();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(u1));
        Assertions.assertTrue(result.contains(u2));
    }

    // --- createUser ---
    @Test
    void createUserNull() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(null)
        );
        Assertions.assertEquals("user should not be null", ex.getMessage());
    }

    @Test
    void createUserAlreadyHasId() {
        User user = new User("n", "e");
        user.setId(42L);
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );
        Assertions.assertEquals("user should not already have an ID!", ex.getMessage());
    }

    @Test
    void createUserValid() {
        User user = new User("n", "e");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        Assertions.assertEquals(user, result);
        verify(userRepository).save(user);
    }

    // --- updateUser ---
    @Test
    void updateUserNullUser() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(42L, null)
        );
        Assertions.assertEquals("user should not be null", ex.getMessage());
    }

    @Test
    void updateUserNoId() {
        User user = new User("n", "e"); // id = null

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(42L, user)
        );
        Assertions.assertEquals("user must already be in the db", ex.getMessage());
    }

    @Test
    void updateUserValid() {
        User user = new User("n", "e");
        user.setId(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(42L, user);

        Assertions.assertEquals(user, result);
        verify(userRepository).save(user);
    }

    // --- deleteById ---
    @Test
    void deleteByIdNull() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteById(null)
        );
        Assertions.assertEquals("user must already be in the db", ex.getMessage());
    }

    @Test
    void deleteByIdNotExisting() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteById(42L)
        );
        Assertions.assertEquals("user must already be in the db", ex.getMessage());
    }

    @Test
    void deleteByIdExisting() {
        User user = new User("n", "e");
        user.setId(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        userService.deleteById(42L);

        verify(userRepository).deleteById(42L);
    }
}