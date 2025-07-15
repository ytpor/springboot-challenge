package com.ytpor.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {

    private static final long serialVersionUID = -3725069385853902614L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Name of the attribute", example = "Category name")
    private String name;

    @Schema(description = "Description of the category", example = "Category description")
    private String description;

    @Schema(description = "Bucket", example = "bucket")
    private String bucket;

    @Schema(description = "Object name", example = "2025/07/14/0e3ef9038aed7d10.jpg")
    private String objectName;

    @Schema(description = "Object content type", example = "image/jpeg")
    private String objectContentType;
}
