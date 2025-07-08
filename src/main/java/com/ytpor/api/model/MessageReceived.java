package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessageReceived {
    @Schema(description = "Status", example = "Message received")
    private String status;
}
