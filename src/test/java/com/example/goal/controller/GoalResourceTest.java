package com.example.goal.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class GoalResourceTest {

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private ObjectMapper objectMapper;


    @WithMockUser(username = "testuser10", roles = {"USER"})
    @Test
    void createGoal_withOneTask_returnsGoal() throws Exception {
        // build JSON payload
        Map<String, Object> payload = Map.of(
                "name", "My First Goal",
                "category", "HEALTH",
                "type", "PUBLIC",
                "votesToMarkCompleted",3,
                "tasks", Set.of(
                        Map.of("name", "task1", "difficulty", "DIFFICULT")
                )
        );

        // convert payload to JSON
        String json = objectMapper.writeValueAsString(payload);

        mockMvc.perform(post("/goal/{userId}", 21153L) // use an existing userId from your test DB
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("My First Goal"))
                .andExpect(jsonPath("$.category").value("HEALTH"));}
}