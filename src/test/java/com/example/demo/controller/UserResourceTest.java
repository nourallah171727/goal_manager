package com.example.demo.controller;

import com.example.demo.dto.DTOMapper;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserResource.class)
@Import(DTOMapper.class)
class UserResourceTest {

    @Autowired
    private MockMvc httpSimulator;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoSpyBean
    private DTOMapper dtoMapper;

    // --- create user ---
    @Test
    void testCreateUserSuccess() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO("Alice", "alice@mail.com");
        User userEntity = new User("Alice", "alice@mail.com");
        userEntity.setId(1L);

        when(userService.createUser(any(User.class))).thenReturn(userEntity);

        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@mail.com"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void testCreateUserFail() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO("bad", "bad");

        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("wrong email"));

        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    // --- get user ---
    @Test
    void testGetUserSuccess() throws Exception {
        User userEntity = new User("Bob", "bob@mail.com");
        userEntity.setId(123L);

        when(userService.getUserById(123L)).thenReturn(userEntity);

        httpSimulator.perform(get("/user/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@mail.com"));

        verify(userService).getUserById(123L);
    }

    @Test
    void testGetUserFail() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new IllegalArgumentException("not found"));

        httpSimulator.perform(get("/user/999"))
                .andExpect(status().isBadRequest());
    }

    // --- get all users ---
    @Test
    void testGetAllUsersSuccess() throws Exception {
        User u1 = new User("u1", "u1@mail.com");
        User u2 = new User("u2", "u2@mail.com");
        List<User> users = List.of(u1, u2);

        when(userService.getUsers()).thenReturn(users);

        httpSimulator.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("u1"))
                .andExpect(jsonPath("$[0].email").value("u1@mail.com"))
                .andExpect(jsonPath("$[1].username").value("u2"))
                .andExpect(jsonPath("$[1].email").value("u2@mail.com"));

        verify(userService).getUsers();
    }

    // --- update user ---
    @Test
    void testUpdateUserSuccess() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated", "updated@mail.com");
        User userEntity = new User("Updated", "updated@mail.com");
        userEntity.setId(42L);

        when(userService.updateUser(eq(42L), any(User.class))).thenReturn(userEntity);

        httpSimulator.perform(put("/user/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));

        verify(userService).updateUser(eq(42L), any(User.class));
    }

    @Test
    void testUpdateUserFail() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("bad", "bad");

        when(userService.updateUser(eq(42L), any(User.class)))
                .thenThrow(new IllegalArgumentException("user not found"));

        httpSimulator.perform(put("/user/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    // --- delete user ---
    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteById(42L);

        httpSimulator.perform(delete("/user/42"))
                .andExpect(status().isNoContent());

        verify(userService).deleteById(42L);
    }

    @Test
    void testDeleteUserFail() throws Exception {
        doThrow(new IllegalArgumentException("user not found"))
                .when(userService).deleteById(42L);

        httpSimulator.perform(delete("/user/42"))
                .andExpect(status().isBadRequest());

        verify(userService).deleteById(42L);
    }
}