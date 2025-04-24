package com.ytpor.api.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemAttributeCreateDTO {

    @NotNull(message = "Name must not be null.")
    @Column(unique = true)
    @Size(max = 255)
    @Schema(description = "Name of the attribute", example = "Attribute name")
    private String name;

    @Size(max = 255)
    @Schema(description = "Description of the attribute", example = "Attribute description")
    private String description;
}
