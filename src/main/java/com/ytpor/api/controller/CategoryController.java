package com.ytpor.api.controller;

import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.MissingRequestBodyException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.service.CategoryService;
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
@RequestMapping("/api/v1/category")
@Tag(name = "Category")
public class CategoryController {

    private static final String REQUEST_BODY_MISSING = "Request body is missing.";

    // Declare the service as final to ensure its immutability
    private final CategoryService categoryService;

    // Use constructor-based dependency injection
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get categories", description = "Retrieve a list of categories")
    public ResponseEntity<Page<Category>> getAllCategories(
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Category> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Retrieve an category")
    public ResponseEntity<Category> getOneCategory(@PathVariable long id) {
        return ResponseEntity.ok(categoryService.getOneCategory(id));
    }

    @PostMapping
    @Operation(summary = "Add category", description = "Add an category")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Category> createCategory(@Valid @RequestBody(required = false) CategoryCreateDTO createDTO) {
        if (createDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return new ResponseEntity<>(categoryService.createCategory(createDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an category")
    public ResponseEntity<Category> updateCategory(@PathVariable long id,
            @RequestBody(required = false) CategoryUpdateDTO updateDTO) {
        if (updateDTO == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }
        return ResponseEntity.ok(categoryService.updateCategory(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete an category")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
