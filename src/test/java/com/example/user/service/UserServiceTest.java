package com.example.user.service;
import com.example.model.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void getUserByIdNotExisting() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(42L)
        );

        assertEquals("no user with such ID", ex.getMessage());
    }

    @Test
    void getUserByIdExisting() {
        User user = new User("name", "email");
        user.setId(42L);

        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(42L);

        assertEquals("name", result.getUsername());
        assertEquals("email", result.getEmail());
    }

    // --- getUsers ---
    @Test
    void getUsersReturnsAll() {
        User u1 = new User("n1", "e1");
        User u2 = new User("n2", "e2");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.getUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains(u1));
        assertTrue(result.contains(u2));
    }

    // --- createUser ---
    @Test
    void createUserValid() {
        User user = new User("n", "e");

        when(userRepository.existsByUsername("n")).thenReturn(false);
        when(userRepository.existsByEmail("e")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("n", result.getUsername());
        assertEquals("e", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void createUserFailSameUserName() {
        User user = new User("n", "anotheremail");

        when(userRepository.existsByUsername("n")).thenReturn(true);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );

        assertEquals("name already used", e.getMessage());
    }

    @Test
    void createUserFailSameEmail() {
        User user = new User("name2", "e");

        when(userRepository.existsByUsername("name2")).thenReturn(false);
        when(userRepository.existsByEmail("e")).thenReturn(true);

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );

        assertEquals("email already used", e.getMessage());
    }

    // --- updateUser ---
    @Test
    void updateUserNoIdInDb() {
        User user = new User("n", "e");
        user.setId(42L);

        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(42L, user)
        );

        assertEquals("user should already be in db", ex.getMessage());
    }

    @Test
    void updateUserValid() {
        User oldUser = new User("old", "old@gmail.com");
        oldUser.setId(1L);
        User updatedUser = new User("n", "e");
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsByUsername("n")).thenReturn(false);
        when(userRepository.existsByEmail("e")).thenReturn(false);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("n", result.getUsername());
        assertEquals("e", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(updatedUser);
    }

    // --- deleteById ---
    @Test
    void deleteByIdNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteById(null)
        );

        assertEquals("user must already be in the db", ex.getMessage());
    }

    @Test
    void deleteByIdNotExisting() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteById(42L)
        );

        assertEquals("user must already be in the db", ex.getMessage());
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