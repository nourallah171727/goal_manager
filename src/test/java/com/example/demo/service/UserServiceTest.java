package com.example.demo.service;
import com.example.demo.model.Goal;
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
    //assumptions from validation layer:
    //username and email are never null
    //goals set is always initialized to an empty hashset , every attempt to add goals through user api is automatically ignored

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }
    //validateUser
    @Test
    void validateUserNull(){
        IllegalArgumentException ex=Assertions.assertThrows(IllegalArgumentException.class,()->userService.validateUser(null));
        Assertions.assertEquals("user is not valid",ex.getMessage());
    }
    @Test
    void validateUserHasId(){
        User user=new User();
        user.setId(1L);
        IllegalArgumentException ex=Assertions.assertThrows(IllegalArgumentException.class,()->userService.validateUser(user));
        Assertions.assertEquals("user is not valid",ex.getMessage());
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
    void createUserValid() {
        User user = new User("n", "e");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        Assertions.assertEquals(user, result);
        verify(userRepository).save(user);
    }
    @Test
    void createUserFailSameUserName() {
        User user = new User("n", "e");
        userService.createUser(user);
        User userToTest=new User("n","anotheremail");
        when(userRepository.existsByUsername("n")).thenReturn(true);
        IllegalArgumentException e=Assertions.assertThrows(IllegalArgumentException.class,()->userService.createUser(userToTest));
        Assertions.assertEquals("name already used",e.getMessage());
    }
    @Test
    void createUserFailSameEmail(){
        User user = new User("name1", "e");
        userService.createUser(user);
        User userToTest=new User("name2","e");
        when(userRepository.existsByUsername("name2")).thenReturn(false);
        when(userRepository.existsByEmail("e")).thenReturn(true);
        IllegalArgumentException e=Assertions.assertThrows(IllegalArgumentException.class,()->userService.createUser(userToTest));
        Assertions.assertEquals("email already used",e.getMessage());
    }

    // --- updateUser ---


    @Test
    void updateUserNoId() {
        User user = new User("n", "e"); // id = null

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(42L, user)
        );
        Assertions.assertEquals("user should already be in db", ex.getMessage());
    }

    @Test
    void updateUserValid() {
        User oldUser=new User("old","old@gmail.com");
        User updatedUser = new User("n", "e");
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        Assertions.assertEquals(updatedUser, result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(updatedUser);
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