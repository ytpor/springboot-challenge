package com.ytpor.api.model;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EncryptionEncryptDTO {
    @NotNull(message = "Plain text must not be null.")
    @Schema(description = "Plain text to be encrypted", example = "plainText")
    private String plainText;
}
