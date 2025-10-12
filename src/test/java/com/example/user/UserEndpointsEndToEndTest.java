package com.example.user;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserEndpointsEndToEndTest {
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private MockMvc mockMvc;
    //user creation post request test
    @Test
    void userCreateSuccess() throws Exception{
        // given
        String userJson = """
            {
                "username": "someName",
                "email": "someName@example.com",
                "password": "password123"
            }
            """;

        // when + then
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("someName"))
                .andExpect(jsonPath("$.email").value("someName@example.com"));

        // verify the user actually got saved in DB
        assertEquals(1, userRepository.count());
        assertEquals("someName", userRepository.findAll().get(0).getUsername());
    }
    @Test
    void userCreateValidationFails() throws Exception {
        String invalidJson = """
        {
            "username": "hey",
            "email": "not-an-email",
            "password": ""
        }
        """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    void userCreateWithUnknownField() throws Exception {
        String unknownFieldJson = """
        {
            "username": "Alice",
            "email": "alice@example.com",
            "password": "password123",
            "extraField": "oops"
        }
        """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unknownFieldJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    void userCreateWithMalformedJson() throws Exception {
        String malformedJson = """
        {
            "username": "Alice",
            "email": "alice@example.com",
            "password": "password123"   // missing closing brace
        """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    //get request test
    @Test
    @WithMockUser(username = "someOtherUser", roles = {"USER"})
    void getUserByIdSuccess() throws Exception {
        User user=new User("test","test@example.com","password123");
        user.setRole("USER");
        User saved = userRepository.save(user);

        mockMvc.perform(get("/user/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(print());
    }
    //get all
    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void getAllUsersSuccess() throws Exception {
        User user1=new User("user1","user1@example.com","somePass");
        user1.setRole("USER");
        User user2=new User("user2","user2@example.com","somePass");
        user2.setRole("USER");
        userRepository.save(user1);
        userRepository.save(user2);

        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andDo(print());
    }
    //PUT
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void updateUserSuccess() throws Exception {
        User user=new User("user","user@example.com","somePassword");
        user.setRole("USER");
        User saved = userRepository.save(user);

        String updateJson = """
        {
            "username": "userUpdated",
            "email": "user.updated@example.com",
            "password":"otherPassword"
        }
        """;

        mockMvc.perform(put("/user/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("userUpdated"))
                .andExpect(jsonPath("$.email").value("user.updated@example.com"))
                .andDo(print());

        assertEquals("userUpdated", userRepository.findById(saved.getId()).orElseThrow().getUsername());
    }
    //delete user
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteUserSuccess() throws Exception {
        User user=new User("user","user@example.com","password");
        user.setRole("USER");
        User saved = userRepository.save(user);

        mockMvc.perform(delete("/user/{id}", saved.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertEquals(0, userRepository.count());
    }
    //follow
    @Test
    @WithMockUser(username = "Follower", roles = {"USER"})
    void followUserSuccess() throws Exception {
        User follower =new User("Follower", "follower@example.com", "pass");
        follower.setRole("USER");

        User followee = new User("Followee", "followee@example.com", "pass");
        followee.setRole("USER");

        userRepository.save(follower);
        userRepository.save(followee);

        mockMvc.perform(post("/user/{followerId}/follow/{followeeId}", follower.getId(), followee.getId()))
                .andExpect(status().isOk())
                .andDo(print());
    }
    //unfollow
    @Test
    @WithMockUser(username = "Follower", roles = {"USER"})

    void unfollowUserSuccess() throws Exception {
        User follower=new User("Follower", "follower@example.com", "pass");
        follower.setRole("USER");
        User followee = new User("Followee","followee@exampke.com","pass");
        followee.setRole("USER");
        userRepository.save(followee);
        userRepository.save(follower);
        followee.getFollowers().add(follower);
        userRepository.save(followee);


        mockMvc.perform(delete("/user/{followerId}/unfollow/{followeeId}", follower.getId(), followee.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
