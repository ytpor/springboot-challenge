package com.ytpor.api.service;

import com.ytpor.api.model.Encryption;
import com.ytpor.api.model.EncryptionDecryptDTO;
import com.ytpor.api.model.EncryptionEncryptDTO;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EncryptionServiceTest {

    @Mock
    private StringEncryptor encryptor;

    @InjectMocks
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEncrypt() {
        EncryptionEncryptDTO encryptDTO = new EncryptionEncryptDTO();
        encryptDTO.setPlainText("plainText");

        when(encryptor.encrypt("plainText")).thenReturn("encryptedText");

        Encryption result = encryptionService.encrypt(encryptDTO);

        assertEquals("plainText", result.getPlainText());
        assertEquals("encryptedText", result.getEncryptedText());
    }

    @Test
    void testDecrypt() {
        EncryptionDecryptDTO decryptDTO = new EncryptionDecryptDTO();
        decryptDTO.setEncryptedText("encryptedText");

        when(encryptor.decrypt("encryptedText")).thenReturn("plainText");

        Encryption result = encryptionService.decrypt(decryptDTO);

        assertEquals("encryptedText", result.getEncryptedText());
        assertEquals("plainText", result.getPlainText());
    }
}
