package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.model.auth.AuthRequest;
import com.ytpor.api.model.auth.AuthResponse;
import com.ytpor.api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_returnsAuthToken() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest("testuser", "testpass");
        AuthResponse mockResponse = new AuthResponse("mocked-jwt-token", null, null);

        when(authService.authenticate(any(AuthRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }
}
