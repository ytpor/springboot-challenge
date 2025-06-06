package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.model.*;
import com.ytpor.api.service.EncryptionService;
import com.ytpor.api.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EncryptionService encryptionService;

    @MockitoBean
    private PasswordService passwordService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateSerialVersionUID() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tool/random-uid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.uid").exists());
    }

    @Test
    void testEncrypt() throws Exception {
        EncryptionEncryptDTO encryptDTO = new EncryptionEncryptDTO();
        encryptDTO.setPlainText("Test Plain Text");

        Encryption encryption = new Encryption();
        encryption.setEncryptedText("Encrypted Text");

        when(encryptionService.encrypt(any(EncryptionEncryptDTO.class))).thenReturn(encryption);

        mockMvc.perform(MockMvcRequestBuilders.post("/tool/encrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encryptDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.encryptedText").value("Encrypted Text"));

        verify(encryptionService, times(1)).encrypt(any(EncryptionEncryptDTO.class));
    }

    @Test
    void testEncrypt_MissingRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tool/encrypt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testDecrypt() throws Exception {
        EncryptionDecryptDTO decryptDTO = new EncryptionDecryptDTO();
        decryptDTO.setEncryptedText("Encrypted Text");

        Encryption encryption = new Encryption();
        encryption.setPlainText("Decrypted Text");

        when(encryptionService.decrypt(any(EncryptionDecryptDTO.class))).thenReturn(encryption);

        mockMvc.perform(MockMvcRequestBuilders.post("/tool/decrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decryptDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.plainText").value("Decrypted Text"));

        verify(encryptionService, times(1)).decrypt(any(EncryptionDecryptDTO.class));
    }

    @Test
    void testDecrypt_MissingRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tool/decrypt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testHashString() throws Exception {
        PasswordEncodeDTO hashDTO = new PasswordEncodeDTO();
        hashDTO.setPlainText("Test Plain Text");

        Password password = new Password();
        password.setHash("Test Hash");

        when(passwordService.encode(any(PasswordEncodeDTO.class))).thenReturn(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/tool/encode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hashDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hash").value("Test Hash"));

        verify(passwordService, times(1)).encode(any(PasswordEncodeDTO.class));
    }

    @Test
    void testHashString_MissingRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tool/encode")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testMatchString() throws Exception {
        PasswordMatchDTO matchDTO = new PasswordMatchDTO();
        matchDTO.setPlainText("Test Plain Text");
        matchDTO.setHash("Test Hash");

        Password password = new Password();
        password.setMatch(true);

        when(passwordService.match(any(PasswordMatchDTO.class))).thenReturn(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/tool/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matchDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.match").value(true));

        verify(passwordService, times(1)).match(any(PasswordMatchDTO.class));
    }

    @Test
    void testMatchString_MissingRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tool/match")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
