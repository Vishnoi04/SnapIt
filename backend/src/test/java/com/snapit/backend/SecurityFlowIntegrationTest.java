package com.snapit.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "snapit.jwt.secret=c25hcGl0LXRlc3Qta2V5LWZvci1qd3Qtc2lnbmluZy0zMg==")
@AutoConfigureMockMvc
@Transactional
class SecurityFlowIntegrationTest {
    private static final String PASSWORD = "StrongPass123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loggedInUsersCanOnlyReadTheirOwnSavedItems() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String ownerToken = registerAndLogin("owner" + suffix, "owner" + suffix + "@example.test");
        String otherToken = registerAndLogin("other" + suffix, "other" + suffix + "@example.test");

        mockMvc.perform(get("/api/saved-items"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());

        mockMvc.perform(post("/api/saved-items")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Private note",
                                  "contentType": "TEXT",
                                  "description": "Owned by another user",
                                  "extractedText": "secret text"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult otherItemsResult = mockMvc.perform(get("/api/saved-items")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode otherItems = objectMapper.readTree(otherItemsResult.getResponse().getContentAsString());
        long savedItemId = otherItems.path("content").path(0).path("id").asLong();
        assertNotEquals(0, savedItemId);

        mockMvc.perform(get("/api/saved-items/{id}", savedItemId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("not found")));

        mockMvc.perform(get("/api/saved-items/{id}", savedItemId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Private note"));
    }

    @Test
    void corsAllowsConfiguredViteOriginAndRejectsUnconfiguredOrigin() throws Exception {
        mockMvc.perform(options("/api/saved-items")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

        mockMvc.perform(options("/api/saved-items")
                        .header("Origin", "https://untrusted.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void regularUserCannotChangeSubscriptionPlans() throws Exception {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String token = registerAndLogin("member" + suffix, "member" + suffix + "@example.test");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/subscriptions/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plan": "PREMIUM",
                                  "expiresAt": "2099-01-01T00:00:00"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied."));
    }

    private String registerAndLogin(String username, String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "username", username,
                                "email", email,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", email,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        return objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("accessToken")
                .asText();
    }
}
