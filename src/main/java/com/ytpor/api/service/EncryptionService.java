package com.ytpor.api.service;

import com.ytpor.api.model.Encryption;
import com.ytpor.api.model.EncryptionDecryptDTO;
import com.ytpor.api.model.EncryptionEncryptDTO;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private final StringEncryptor encryptor;

    public EncryptionService(StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public Encryption encrypt(EncryptionEncryptDTO encryptDTO) {
        Encryption encryption = new Encryption();
        encryption.setEncryptedText(encryptor.encrypt(encryptDTO.getPlainText()));
        encryption.setPlainText(encryptDTO.getPlainText());

        return encryption;
    }

    public Encryption decrypt(EncryptionDecryptDTO decryptDTO) {
        Encryption encryption = new Encryption();
        encryption.setEncryptedText(decryptDTO.getEncryptedText());
        encryption.setPlainText(encryptor.decrypt(decryptDTO.getEncryptedText()));

        return encryption;
    }
}

