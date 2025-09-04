package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.GoalService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserResource.class)
public class UserResourceTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc httpSimulator;

    @MockitoBean
    private UserService service;

    @Test
    void createUserSuccessful() throws Exception{ //throws Exception
        User user= new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@yahoo.de");
        when(service.createUser(user)).thenReturn(user);
        httpSimulator.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Abderrahmen Firas Ben Hmidene"))
                .andExpect(jsonPath("$.email").value("thisisanemail@yahoo.de"));
        verify(service).createUser(any(User.class));
    }

    @Test
    void createUserFail() throws Exception{
        User user= new User("Abderrahmen Firas Ben Hmidene", "thisisnotanemail");
        when(service.createUser(user)).thenThrow(new IllegalArgumentException("wrong email"));
        httpSimulator.perform(post("/user"))
                .andExpect(status().isBadRequest());
        verify(service).createUser(any(User.class));
    }

    @Test
    void getUserSuccess() throws Exception{
        User user = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@gmail.com");
        user.setId(1235586L);
        when(service.getUserById(1235586L)).thenReturn(user);
        httpSimulator.perform(get("/user/1235586"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Abderrahmen Firas Ben Hmidene"))
                .andExpect(jsonPath("$.email").value("thisisanemail@yahoo.de"))
                .andExpect(jsonPath("$.id").value(1235586L));
        verify(service).getUserById(1235586L);
    }
    @Test
    void getUserFail() throws Exception{

    }

}
