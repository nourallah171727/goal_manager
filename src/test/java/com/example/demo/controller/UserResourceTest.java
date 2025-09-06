package com.example.demo.controller;

import com.example.demo.model.User;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    void testCreateUserSuccessful() throws Exception { //throws Exception
        User user = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@yahoo.de");
        when(service.createUser(user)).thenReturn(user);
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Abderrahmen Firas Ben Hmidene"))
                .andExpect(jsonPath("$.email").value("thisisanemail@yahoo.de"));
        verify(service).createUser(any(User.class));
    }

    @Test
    void testCreateUserFail1() throws Exception {
        User user = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@yahoo.fr");
        user.setId(1458755L);
        when(service.createUser(user)).thenThrow(new IllegalArgumentException("wrong email"));
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
        verify(service).createUser(any(User.class));
    }

    @Test
    void testCreateUserFail2() throws Exception {
        User user = null;
        when(service.createUser(null)).thenThrow(new IllegalArgumentException("wrong email"));
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserFail3() throws Exception {
        User user1 = new User("unknown1", "thisisanemail1@gmail.com");
        User user2 = new User("unknown1", "thisistheemail2@gmail.com");
        user1.setId(null);
        user2.setId(null);
        System.out.println("-----------");
        System.out.println(user1.toString());
        System.out.println(user2.toString());
        AtomicBoolean called = new AtomicBoolean(false);
        when(service.createUser(any(User.class))).thenAnswer(invocation -> {
            if (called.get()) {
                throw new IllegalArgumentException("name already used");
            } else {
                called.set(true);
                return invocation.getArgument(0);
            }
        });

        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("unknown1"))
                .andExpect(jsonPath("$.email").value("thisisanemail1@gmail.com"));
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest());
        verify(service, times(2)).createUser(any(User.class));
    }

    @Test
    void testCreateUserFail4() throws Exception {
        User user1 = new User("unknown1", "thisisanemail1@gmail.com");
        User user2 = new User("unknown2", "thisistheemail1@gmail.com");
        AtomicBoolean called = new AtomicBoolean(false);
        when(service.createUser(any(User.class))).thenAnswer(invocation -> {
            if (called.get()) {
                throw new IllegalArgumentException("name already used");
            } else {
                called.set(true);
                return invocation.getArgument(0);
            }
        });
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("unknown1"))
                .andExpect(jsonPath("$.email").value("thisisanemail1@gmail.com"));
        httpSimulator.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest());
        verify(service, times(2)).createUser(any(User.class));
    }


    @Test
    void testGetUserSuccess() throws Exception {
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
    void testGetUserFail() throws Exception {
        when(service.getUserById(4585585L)).thenThrow(new IllegalArgumentException("no user with such id"));
        httpSimulator.perform(get("/user/4585585L"))
                .andExpect(status().isBadRequest());
        verify(service).getUserById(4585585L);
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        User user1 = new User("unknown1", "thisisanemail1@gmail.com");
        User user2 = new User("unknown2", "thisistheemail2@gmail.com");
        List<User> list = new LinkedList<>();
        list.add(user1);
        list.add(user2);
        when(service.getUsers()).thenReturn(list);
        httpSimulator.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("unknown1"))
                .andExpect(jsonPath("$[0].email").value("thisisanemail1@gmail.com"))
                .andExpect(jsonPath("$[1].name").value("unknown2"))
                .andExpect(jsonPath("$[1].email").value("thisisanemail2@gmail.com"));
        verify(service).getUsers();
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        User newUser = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail2@gmail.com");
        newUser.setId(1234544L);
        when(service.updateUser(1234544L, newUser)).thenReturn(newUser);
        httpSimulator.perform(put("/user/1234544")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1234544))
                .andExpect(jsonPath("$.name").value("Abderrahmen Firas Ben Hmidene"))
                .andExpect(jsonPath("$.email").value("thisisanemail2@gmail.com"));
        verify(service).updateUser(1234544L, any(User.class));
    }

    @Test
    void testUpdateUserFail1() throws Exception {
        when(service.updateUser(1215487L, null)).thenThrow(new IllegalArgumentException("user should not be null"));
        httpSimulator.perform(put("/user/1215487")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
        verify(service).updateUser(1215487L, null);
    }

    @Test
    void testUpdateUserFail2() throws Exception {
        User user = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@gmail.com");
        user.setId(null);
        when(service.updateUser(1215487L, user)).thenThrow(new IllegalArgumentException("user must already be in the db"));
        httpSimulator.perform(put("/user/1215487")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
        verify(service).updateUser(1215487L, any(User.class));
    }

    @Test
    void testUpdateUserFail3() throws Exception {
        User user = new User("Abderrahmen Firas Ben Hmidene", "thisisanemail@gmail.com");
        user.setId(1215487L);
        when(service.updateUser(1215487L, user)).thenThrow(new IllegalArgumentException("user must already be in the db"));
        httpSimulator.perform(put("/user/1215487")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
        verify(service).updateUser(1215487L, any(User.class));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(service).deleteById(1235586L);
        httpSimulator.perform(delete("/user/1235586"))
                .andExpect(status().isNoContent());
        verify(service).deleteById(1235586L);
    }

    @Test
    void testDeleteUserFail() throws Exception {
        doThrow(new IllegalArgumentException("user must already be in the db"))
                .when(service).deleteById(1235586L);
        httpSimulator.perform(delete("/user/1235586"))
                .andExpect(status().isBadRequest());
        verify(service).deleteById(1235586L);
    }
}