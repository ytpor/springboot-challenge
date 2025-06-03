package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Password {
    @Schema(description = "Plain text to be encoded", example = "plainText")
    private String plainText;

    @Schema(description = "Encoded plain text", example = "$2a$10$5Ike5eHLWlXd/EbAg0y76.FwdRzy0pKm7wSLoMAYNLsc9Es/XxEmG")
    private String hash;

    @Schema(description = "Plain text matches encoded plain text", example = "true")
    private boolean match;
}
