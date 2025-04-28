package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Encryption {
    @Schema(description = "Plain text to be encrypted", example = "plainText")
    private String plainText;

    @Schema(description = "Encrypted text to be decrypted", example = "encryptedText")
    private String encryptedText;
}
