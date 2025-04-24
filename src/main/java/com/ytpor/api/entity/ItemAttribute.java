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
@Table(name = "item_attribute")
public class ItemAttribute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Attribute ID", example = "1")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Name of the attribute", example = "Attribute name")
    private String name;

    @Schema(description = "Description of the attribute", example = "Attribute description")
    private String description;

}
