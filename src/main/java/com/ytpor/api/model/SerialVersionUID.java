package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SerialVersionUID {
    @Schema(description = "Random Serial Version UID", example = "2475049792676714452L")
    private String uid;
}
