package com.ytpor.api.service;

import com.ytpor.api.model.Password;
import com.ytpor.api.model.PasswordEncodeDTO;
import com.ytpor.api.model.PasswordMatchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordServiceTest {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private PasswordEncodeDTO encodeDTO;
    private PasswordMatchDTO matchDTO;

    @BeforeEach
    void setUp() {
        encodeDTO = new PasswordEncodeDTO();
        encodeDTO.setPlainText("testPassword");

        matchDTO = new PasswordMatchDTO();
        matchDTO.setPlainText("testPassword");
        matchDTO.setHash(passwordEncoder.encode("testPassword"));
    }

    @Test
    void testEncode() {
        Password encodedPassword = passwordService.encode(encodeDTO);
        assertNotNull(encodedPassword);
        assertEquals("testPassword", encodedPassword.getPlainText());
        assertTrue(passwordEncoder.matches("testPassword", encodedPassword.getHash()));
        assertTrue(encodedPassword.isMatch());
    }

    @Test
    void testMatch() {
        Password matchedPassword = passwordService.match(matchDTO);
        assertNotNull(matchedPassword);
        assertEquals("testPassword", matchedPassword.getPlainText());
        assertEquals(matchDTO.getHash(), matchedPassword.getHash());
        assertTrue(matchedPassword.isMatch());
    }
}
