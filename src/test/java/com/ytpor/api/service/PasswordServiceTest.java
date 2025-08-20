package com.ytpor.api.service;

import com.ytpor.api.model.Password;
import com.ytpor.api.model.PasswordEncodeDTO;
import com.ytpor.api.model.PasswordMatchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    private PasswordEncodeDTO encodeDTO;
    private PasswordMatchDTO matchDTO;
    private PasswordMatchDTO notMatchDTO;
    private static final String PLAIN_TEXT_PASSWORD = "myPassword123";
    private static final String WRONG_TEXT_PASSWORD = "wrongPassword";
    private static final String HASHED_PASSWORD = "hashedPassword_mock";

    @BeforeEach
    void setUp() {
        encodeDTO = new PasswordEncodeDTO();
        encodeDTO.setPlainText(PLAIN_TEXT_PASSWORD);

        matchDTO = new PasswordMatchDTO();
        matchDTO.setPlainText(PLAIN_TEXT_PASSWORD);
        matchDTO.setHash(HASHED_PASSWORD);

        notMatchDTO = new PasswordMatchDTO();
        notMatchDTO.setPlainText(WRONG_TEXT_PASSWORD);
        notMatchDTO.setHash(HASHED_PASSWORD);
    }

    @Test
    void testEncode() {
        when(passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);

        Password result = passwordService.encode(encodeDTO);
        assertNotNull(result);
        assertEquals(PLAIN_TEXT_PASSWORD, result.getPlainText());
        assertEquals(HASHED_PASSWORD, result.getHash());
        assertTrue(result.isMatch());
    }

    @Test
    void testMatch() {
        when(passwordEncoder.matches(PLAIN_TEXT_PASSWORD, HASHED_PASSWORD)).thenReturn(true);

        Password result = passwordService.match(matchDTO);
        assertNotNull(result);
        assertEquals(PLAIN_TEXT_PASSWORD, result.getPlainText());
        assertEquals(HASHED_PASSWORD, result.getHash());
        assertTrue(result.isMatch());
    }

    @Test
    void testNotMatch() {
        when(passwordEncoder.matches(WRONG_TEXT_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        Password result = passwordService.match(notMatchDTO);
        assertNotNull(result);
        assertEquals(WRONG_TEXT_PASSWORD, result.getPlainText());
        assertEquals(HASHED_PASSWORD, result.getHash());
        assertFalse(result.isMatch());
    }
}
