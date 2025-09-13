package com.example.demo.service;
import com.example.demo.dto.DTOMapper;
import com.example.demo.dto.UserResponseDTO;
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
import org.mockito.Spy;
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
    @Spy
    private DTOMapper dtoMapper = new DTOMapper();
    @BeforeEach
    void setUp() {
    }

    // --- getUserById ---


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
        Assertions.assertEquals(user.getUsername(), dtoMapper.responseDtoToUser(userService.getUserById(42L)).getUsername());
        Assertions.assertEquals(user.getEmail(), dtoMapper.responseDtoToUser(userService.getUserById(42L)).getEmail());
    }

    // --- getUsers ---
    @Test
    void getUsersReturnsAll() {
        User u1 = new User("n1", "e1");
        User u2 = new User("n2", "e2");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDTO> result = userService.getUsers();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(dtoMapper.userToResponseDTO(u1)));
        Assertions.assertTrue(result.contains(dtoMapper.userToResponseDTO(u2)));
    }

    // --- createUser ---
    @Test
    void createUserValid() {
        User user = new User("n", "e");
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO result = userService.createUser(dtoMapper.userToCreateDTO(user));

        Assertions.assertEquals(user.getUsername(), result.username());
        Assertions.assertEquals(user.getEmail(), result.email());
        verify(userRepository).save(user);
    }
    @Test
    void createUserFailSameUserName() {
        User userToTest=new User("n","anotheremail");
        when(userRepository.existsByUsername("n")).thenReturn(true);
        IllegalArgumentException e=Assertions.assertThrows(IllegalArgumentException.class,()->userService.createUser(dtoMapper.userToCreateDTO(userToTest)));
        Assertions.assertEquals("name already used",e.getMessage());
    }
    @Test
    void createUserFailSameEmail(){
        User userToTest=new User("name2","e");
        when(userRepository.existsByUsername("name2")).thenReturn(false);
        when(userRepository.existsByEmail("e")).thenReturn(true);
        IllegalArgumentException e=Assertions.assertThrows(IllegalArgumentException.class,()->userService.createUser(dtoMapper.userToCreateDTO(userToTest)));
        Assertions.assertEquals("email already used",e.getMessage());
    }

    // --- updateUser ---


    @Test
    void updateUserNoId() {
        User user = new User("n", "e"); // id = null

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(42L,dtoMapper.userToUpdateDTO( user))
        );
        Assertions.assertEquals("user should already be in db", ex.getMessage());
    }

    @Test
    void updateUserValid() {
        User oldUser=new User("old","old@gmail.com");
        oldUser.setId(1L);
        User updatedUser = new User("n", "e");
        updatedUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserResponseDTO result = userService.updateUser(1L, dtoMapper.userToUpdateDTO(updatedUser));

        Assertions.assertEquals(updatedUser.getUsername(), result.username());
        Assertions.assertEquals(updatedUser.getEmail(), result.email());
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