package com.ytpor.api.controller;

import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.model.ItemAttributeCreateDTO;
import com.ytpor.api.model.ItemAttributeUpdateDTO;
import com.ytpor.api.service.ItemAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attribute")
@Tag(name = "Attribute")
public class ItemAttributeController {

    // Declare the service as final to ensure its immutability
    private final ItemAttributeService itemAttributeService;

    // Use constructor-based dependency injection
    public ItemAttributeController(ItemAttributeService itemAttributeService) {
        this.itemAttributeService = itemAttributeService;
    }

    @GetMapping
    @Operation(summary = "Get item attributes", description = "Retrieve a list of item attributes")
    public ResponseEntity<Page<ItemAttribute>> getAllItemAttributes(
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<ItemAttribute> itemAttributes = itemAttributeService.getAllItemAttributes(pageable);
        return ResponseEntity.ok(itemAttributes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item attribute", description = "Retrieve an item attribute")
    public ResponseEntity<ItemAttribute> getItemAttributeById(@PathVariable long id) {
        return ResponseEntity.ok(itemAttributeService.getItemAttributeById(id));
    }

    @PostMapping
    @Operation(summary = "Add item attribute", description = "Add an item attribute")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemAttribute> createItemAttribute(@Valid @RequestBody ItemAttributeCreateDTO createDTO) {
        return new ResponseEntity<>(itemAttributeService.createItemAttribute(createDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update item attribute", description = "Update an item attribute")
    public ResponseEntity<ItemAttribute> updateItemAttribute(@PathVariable long id,
            @RequestBody ItemAttributeUpdateDTO updateDTO) {
        return ResponseEntity.ok(itemAttributeService.updateItemAttribute(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item attribute", description = "Delete an item attribute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteItemAttribute(@PathVariable long id) {
        itemAttributeService.deleteItemAttribute(id);
        return ResponseEntity.noContent().build();
    }
}
