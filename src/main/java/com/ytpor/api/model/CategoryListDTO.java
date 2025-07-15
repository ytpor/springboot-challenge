package com.ytpor.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryListDTO implements Serializable {

    private static final long serialVersionUID = -1900152530970466225L;

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Category name")
    private String name;

    @Schema(description = "Description of the category", example = "Category description")
    private String description;

    @Schema(description = "Created at", example = "2025-07-15 11:05:26.105017")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at", example = "2025-07-15 11:05:26.105017")
    private LocalDateTime updatedAt;
}
