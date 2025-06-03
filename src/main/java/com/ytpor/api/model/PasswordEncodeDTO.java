package com.ytpor.api.model;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordEncodeDTO {
    @NotNull(message = "Plain text must not be null.")
    @Schema(description = "Plain text to be encoded", example = "plainText")
    private String plainText;
}
