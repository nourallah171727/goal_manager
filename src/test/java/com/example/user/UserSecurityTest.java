package com.example.user;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// for simplicity reasons , for now , security tests are able to change the database
//should run everyone separately
@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void updateUserDeniedIfNotAuthenticated() throws Exception {
        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"email\":\"bob@mail.com\"}"))
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    @WithMockUser(username = "bob", roles = "USER")
    void updateUserAllowedIfAuthenticatedWithUserRole() throws Exception {

        // First create the user
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"email\":\"bob@mail.com\",\"password\":\"pwd\"}"))
                .andExpect(status().isCreated());

        // Then update it
        mockMvc.perform(put("/user/207")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bobby\",\"email\":\"bobby@mail.com\",\"password\":\"pwd\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserAllowedIfAuthenticatedWithAdminRole() throws Exception {
        // First create the user
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"email\":\"bob@mail.com\",\"password\":\"pwd\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"email\":\"admin@mail.com\"}"))
                .andExpect(status().isOk());
    }
}