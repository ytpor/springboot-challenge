package com.ytpor.api.model;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordMatchDTO {
    @NotNull(message = "Plain text must not be null.")
    @Schema(description = "Plain text to be encoded", example = "plainText")
    private String plainText;

    @NotNull(message = "Encoded plain text must not be null.")
    @Schema(description = "Encoded plain text", example = "$2a$10$5Ike5eHLWlXd/EbAg0y76.FwdRzy0pKm7wSLoMAYNLsc9Es/XxEmG")
    private String hash;
}
