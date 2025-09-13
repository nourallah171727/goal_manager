package com.example.demo.controller;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserResource.class)
public class UserResourceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc httpSimulator;

    @MockitoBean
    private UserService service;

    // --- create user ---
    @Test
    void testCreateUserSuccessful() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO("Abderrahmen Firas Ben Hmidene", "thisisanemail@yahoo.de");
        UserResponseDTO responseDTO = new UserResponseDTO("Abderrahmen Firas Ben Hmidene", "thisisanemail@yahoo.de");

        when(service.createUser(any(UserCreateDTO.class))).thenReturn(responseDTO);

        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Abderrahmen Firas Ben Hmidene"))
                .andExpect(jsonPath("$.email").value("thisisanemail@yahoo.de"));

        verify(service).createUser(any(UserCreateDTO.class));
    }

    @Test
    void testCreateUserFail() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO("badName", "badMail");
        when(service.createUser(any(UserCreateDTO.class))).thenThrow(new IllegalArgumentException("wrong email"));

        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    // --- get user ---
    @Test
    void testGetUserSuccess() throws Exception {
        UserResponseDTO responseDTO = new UserResponseDTO("Firas", "mail@gmail.com");
        when(service.getUserById(123L)).thenReturn(responseDTO);

        httpSimulator.perform(get("/user/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Firas"))
                .andExpect(jsonPath("$.email").value("mail@gmail.com"));

        verify(service).getUserById(123L);
    }

    @Test
    void testGetUserFail() throws Exception {
        when(service.getUserById(999L)).thenThrow(new IllegalArgumentException("no user with such id"));

        httpSimulator.perform(get("/user/999"))
                .andExpect(status().isBadRequest());

        verify(service).getUserById(999L);
    }

    // --- get all users ---
    @Test
    void testGetAllUsersSuccess() throws Exception {
        List<UserResponseDTO> list = new LinkedList<>();
        list.add(new UserResponseDTO("u1", "u1@mail.com"));
        list.add(new UserResponseDTO("u2", "u2@mail.com"));

        when(service.getUsers()).thenReturn(list);

        httpSimulator.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("u1"))
                .andExpect(jsonPath("$[0].email").value("u1@mail.com"))
                .andExpect(jsonPath("$[1].username").value("u2"))
                .andExpect(jsonPath("$[1].email").value("u2@mail.com"));

        verify(service).getUsers();
    }

    // --- update user ---
    @Test
    void testUpdateUserSuccess() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated", "updated@mail.com");
        UserResponseDTO responseDTO = new UserResponseDTO("Updated", "updated@mail.com");

        when(service.updateUser(eq(42L), any(UserUpdateDTO.class))).thenReturn(responseDTO);

        httpSimulator.perform(put("/user/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));

        verify(service).updateUser(eq(42L), any(UserUpdateDTO.class));
    }

    @Test
    void testUpdateUserFail() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("bad", "bad");
        when(service.updateUser(eq(42L), any(UserUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("user not found"));

        httpSimulator.perform(put("/user/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    // --- delete user ---
    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(service).deleteById(42L);

        httpSimulator.perform(delete("/user/42"))
                .andExpect(status().isNoContent());

        verify(service).deleteById(42L);
    }

    @Test
    void testDeleteUserFail() throws Exception {
        doThrow(new IllegalArgumentException("user not found")).when(service).deleteById(42L);

        httpSimulator.perform(delete("/user/42"))
                .andExpect(status().isBadRequest());

        verify(service).deleteById(42L);
    }
}