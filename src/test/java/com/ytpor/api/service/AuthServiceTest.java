package com.ytpor.api.service;

import com.ytpor.api.model.auth.AuthRequest;
import com.ytpor.api.model.auth.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private Authentication authentication;
    private static final String USERNAME = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String MOCK_JWT_TOKEN = "mocked.jwt.token";
    private static final Long MOCK_EXPIRES_AT = 1672531200000L; // Example timestamp

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest(USERNAME, PASSWORD);

        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // Mock a successful authentication object
        authentication = new UsernamePasswordAuthenticationToken(
            new User(USERNAME, PASSWORD, authorities),
            null
        );

        // Define mock behavior for the authenticationManager and jwtTokenService
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenService.generateToken(any(Authentication.class)))
            .thenReturn(MOCK_JWT_TOKEN);
        when(jwtTokenService.extractExpirationTime(MOCK_JWT_TOKEN))
            .thenReturn(MOCK_EXPIRES_AT);
    }

    @Test
    void testAuthenticate() {
        AuthResponse response = authService.authenticate(authRequest);
        assertNotNull(response);
        assertEquals(MOCK_JWT_TOKEN, response.getToken());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(MOCK_EXPIRES_AT, response.getExpiresAt());
    }
}