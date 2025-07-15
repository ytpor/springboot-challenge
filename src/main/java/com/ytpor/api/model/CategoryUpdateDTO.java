package com.ytpor.api.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CategoryUpdateDTO {

    @Column(unique = true)
    @Size(max = 255)
    @Schema(description = "Name of the category", example = "Category name")
    private String name;

    @Size(max = 255)
    @Schema(description = "Description of the category", example = "Category description")
    private String description;

    @Size(max = 255)
    @Schema(hidden = true)
    private String bucket;

    @Size(max = 255)
    @Schema(hidden = true)
    private String objectName;

    @Size(max = 255)
    @Schema(hidden = true)
    private String objectContentType;
}
