package com.ytpor.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private JwtEncoder encoder;

    @Mock
    private JwtDecoder decoder;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private Authentication authentication;
    private static final String USERNAME = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String MOCK_JWT_TOKEN = "mocked.jwt.token";

    @BeforeEach
    void setUp() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // Mock a successful authentication object
        authentication = new UsernamePasswordAuthenticationToken(
            new User(USERNAME, PASSWORD, authorities),
            null,
            authorities
        );
    }

    @Test
    void testGenerateToken() {
        Jwt mockJwt = Jwt.withTokenValue(MOCK_JWT_TOKEN)
            .header("alg", MacAlgorithm.HS256.getName())
            .claim("sub", USERNAME)
            .build();

        when(encoder.encode(any(JwtEncoderParameters.class)))
            .thenReturn(mockJwt);

        String token = jwtTokenService.generateToken(authentication);
        assertNotNull(token);
        assertEquals(MOCK_JWT_TOKEN, token);
    }

    @Test
    void extractExpirationTime_shouldReturnCorrectExpirationTime() {
        Instant now = Instant.now();
        Instant oneHourFromNow = now.plusSeconds(3600);

        Jwt mockedJwt = Jwt.withTokenValue(MOCK_JWT_TOKEN)
                .claim("exp", oneHourFromNow)
                .header("alg", "HS256")
                .build();

        when(decoder.decode(MOCK_JWT_TOKEN)).thenReturn(mockedJwt);

        Long expirationTime = jwtTokenService.extractExpirationTime(MOCK_JWT_TOKEN);
        assertNotNull(expirationTime);
        assertEquals(oneHourFromNow.toEpochMilli(), expirationTime);
    }
}
