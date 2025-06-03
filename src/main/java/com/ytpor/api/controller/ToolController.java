package com.ytpor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ytpor.api.model.SerialVersionUID;
import com.ytpor.api.exception.MissingRequestBodyException;
import com.ytpor.api.model.Encryption;
import com.ytpor.api.model.EncryptionDecryptDTO;
import com.ytpor.api.model.EncryptionEncryptDTO;
import com.ytpor.api.model.Password;
import com.ytpor.api.model.PasswordEncodeDTO;
import com.ytpor.api.model.PasswordMatchDTO;
import com.ytpor.api.service.EncryptionService;
import com.ytpor.api.service.PasswordService;

@RestController
@RequestMapping("/tool")
@Tag(name = "Tool", description = "Tools used for specific tasks")
@SecurityRequirements()
public class ToolController {

    private static final String REQUEST_BODY_MISSING = "Request body is missing.";

    private final Random random = new Random();

    private final EncryptionService encryptionService;

    private final PasswordService passwordService;

    public ToolController(EncryptionService encryptionService, PasswordService passwordService) {
        this.encryptionService = encryptionService;
        this.passwordService = passwordService;
    }

    @GetMapping("/random-uid")
    @Operation(summary = "Generate UID", description = "Generate Random Serial Version UID")
    public ResponseEntity<SerialVersionUID> generateSerialVersionUID() {
        SerialVersionUID serialVersionUID = new SerialVersionUID();
        serialVersionUID.setUid(random.nextLong() + "L");

        return ResponseEntity.ok(serialVersionUID);
    }

    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt string", description = "Encrypt string. Find password at jasypt.encryptor.password")
    public ResponseEntity<Encryption> encrypt(@Valid @RequestBody(required = false) EncryptionEncryptDTO encryptDTO) {
        if (encryptDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return ResponseEntity.ok(encryptionService.encrypt(encryptDTO));
    }

    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt string", description = "Decrypt string. Find password at jasypt.encryptor.password")
    public ResponseEntity<Encryption> decrypt(@Valid @RequestBody(required = false) EncryptionDecryptDTO decryptDTO) {
        if (decryptDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return ResponseEntity.ok(encryptionService.decrypt(decryptDTO));
    }

    @PostMapping("/encode")
    @Operation(summary = "Encode string", description = "Encode string with BCrypt")
    public ResponseEntity<Password> hashString(@Valid @RequestBody(required = false) PasswordEncodeDTO hashDTO) {
        if (hashDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return ResponseEntity.ok(passwordService.encode(hashDTO));
    }

    @PostMapping("/match")
    @Operation(summary = "Compare string and encoded string", description = "Compare string and encoded string")
    public ResponseEntity<Password> matchString(@Valid @RequestBody(required = false) PasswordMatchDTO matchDTO) {
        if (matchDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return ResponseEntity.ok(passwordService.match(matchDTO));
    }
}
