package com.ytpor.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryViewDTO implements Serializable {

    private static final long serialVersionUID = -6055911235442654329L;

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Category name")
    private String name;

    @Schema(description = "Description of the category", example = "Category description")
    private String description;

    @Schema(description = "Object name", example = "2025/07/14/0e3ef9038aed7d10.jpg")
    private String objectName;

    @Schema(description = "Object URL", example = "http://127.0.0.1:9000/minio-bucket/2025/07/15/0e3ef9038aed7d10.jpg")
    private String objectUrl;

    @Schema(description = "Object content type", example = "image/jpeg")
    private String objectContentType;

    @Schema(description = "Created at", example = "2025-07-15 11:05:26.105017")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at", example = "2025-07-15 11:05:26.105017")
    private LocalDateTime updatedAt;
}
