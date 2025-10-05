package com.tum.goal_manager.user.service;

import com.tum.goal_manager.user.entity.User;
import com.tum.goal_manager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

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
    void updateUserDeniedNotOwner() {
        User existingUser = new User("old", "old@gmail.com");
        existingUser.setId(1L);

        User updatedUser = new User("new", "new@gmail.com");
        updatedUser.setId(1L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("stealer");
        when(authentication.getAuthorities()).thenReturn(List.of()); // no roles
        User stealer=new User("stealer","stealer@gmail.com");
        stealer.setId(2L);
        when(userRepository.findByUsername("stealer")).thenReturn(Optional.of(stealer));
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(1L, updatedUser));
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateUserValidAsOwner() {
        User existingUser = new User("old", "old@gmail.com");
        existingUser.setId(1L);

        User updatedUser = new User("new", "new@gmail.com");
        updatedUser.setId(1L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("old");
        when(authentication.getAuthorities()).thenReturn(List.of());
        when(userRepository.findByUsername("old")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("new", result.getUsername());
        assertEquals("new@gmail.com", result.getEmail());
        SecurityContextHolder.clearContext();
    }
    @Test
    void updateUserValidAsAdmin() {
        User existingUser = new User("old", "old@gmail.com");
        existingUser.setId(1L);

        User updatedUser = new User("new", "new@gmail.com");
        updatedUser.setId(1L);

        // mock authentication as admin
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities())
                .thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        User admin = new User("admin", "admin@gmail.com");
        admin.setId(99L);
        admin.setRole("ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("new", result.getUsername());
        assertEquals("new@gmail.com", result.getEmail());

        SecurityContextHolder.clearContext();
    }

    // --- deleteById ---
    @Test
    void deleteByIdDeniedIfNotOwner() {
        User existingUser = new User("u", "e");
        existingUser.setId(1L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("other");
        User other=new User("other","o@gmail.com");
        other.setId(2L);
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(other));
        assertThrows(AccessDeniedException.class, () -> userService.deleteById(1L));
    }

    @Test
    void deleteByIdValidAsOwner() {
        User existingUser = new User("me", "me@gmail.com");
        existingUser.setId(1L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("me");
        when(userRepository.findByUsername("me")).thenReturn(Optional.of(existingUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    // --- follow ---
    @Test
    void followValidAsUser() {
        User follower = new User("f", "f@gmail.com");
        follower.setId(1L);
        User followee = new User("fe", "fe@gmail.com");
        followee.setId(2L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("f");
        when(userRepository.findByUsername("f")).thenReturn(Optional.of(follower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        userService.follow(1L, 2L);

        assertTrue(follower.getFollowing().contains(followee));
        assertTrue(followee.getFollowers().contains(follower));
        verify(userRepository).save(follower);
    }

    @Test
    void followDeniedIfSelfFollow() {
        User follower = new User("f", "f@gmail.com");
        follower.setId(1L);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("f");
        when(userRepository.findByUsername("f")).thenReturn(Optional.of(follower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));

        assertThrows(IllegalArgumentException.class, () -> userService.follow(1L, 1L));
    }

    // --- unfollow ---
    @Test
    void unfollowValidAsUser() {
        User follower = new User("f", "f@gmail.com");
        follower.setId(1L);
        User followee = new User("fe", "fe@gmail.com");
        followee.setId(2L);
        follower.getFollowing().add(followee);
        followee.getFollowers().add(follower);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("f");
        when(userRepository.findByUsername("f")).thenReturn(Optional.of(follower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        userService.unfollow(1L, 2L);

        assertFalse(follower.getFollowing().contains(followee));
        assertFalse(followee.getFollowers().contains(follower));
        verify(userRepository).save(follower);
    }
}