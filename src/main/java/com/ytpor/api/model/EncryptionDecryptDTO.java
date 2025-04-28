package com.ytpor.api.model;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EncryptionDecryptDTO {
    @NotNull(message = "Encrypted text must not be null.")
    @Schema(description = "Encrypted text to be decrypted", example = "encryptedText")
    private String encryptedText;
}
