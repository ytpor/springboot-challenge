package com.ytpor.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.FieldDontExistException;
import com.ytpor.api.exception.InvalidJsonException;
import com.ytpor.api.exception.MissingRequestBodyException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryListDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.model.CategoryViewDTO;
import com.ytpor.api.service.CategoryService;
import com.ytpor.api.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Category")
public class CategoryController {

    private static final String REQUEST_BODY_MISSING = "Request body is missing.";
    private static final String REQUEST_INVALID_FIELD = "Invalid field in request body: ";
    private static final String REQUEST_INVALID_JSON = "Data sent is not valid JSON.";

    // Declare the service as final to ensure its immutability
    private final CategoryService categoryService;
    private final MinioService minioService;

    // Use constructor-based dependency injection
    public CategoryController(
        CategoryService categoryService,
        MinioService minioService
    ) {
        this.categoryService = categoryService;
        this.minioService = minioService;
    }

    @GetMapping
    @Operation(summary = "Get categories", description = "Retrieve a list of categories")
    public ResponseEntity<Page<CategoryListDTO>> getAllCategories(
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<CategoryListDTO> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category", description = "Retrieve a category")
    public ResponseEntity<CategoryViewDTO> getOneCategory(@PathVariable long id) {
        return ResponseEntity.ok(categoryService.getOneCategory(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add category", description = "Add a category with optional file upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryViewDTO> createCategory(
        @Parameter(
            description = "Category data as JSON string",
            schema = @Schema(implementation = CategoryCreateDTO.class)
        )
        @Valid @RequestPart() String data,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        if (data == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }

        CategoryCreateDTO createDTO;
        try {
            createDTO = new ObjectMapper().readValue(data, CategoryCreateDTO.class);
        } catch (UnrecognizedPropertyException e) {
            throw new FieldDontExistException(REQUEST_INVALID_FIELD + e.getPropertyName());
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(REQUEST_INVALID_JSON);
        }

        // Create the category first (without file path)
        Category createdCategory = categoryService.createCategory(createDTO);

        if (file != null && !file.isEmpty()) {
            try {
                String bucket = minioService.getBucket();
                String filePath = minioService.uploadFileAndGetPath(file);
                String contentType = file.getContentType();
                CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
                updateDTO.setBucket(bucket);
                updateDTO.setObjectName(filePath);
                updateDTO.setObjectContentType(contentType);
                categoryService.updateCategory(createdCategory.getId(), updateDTO);
            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        return new ResponseEntity<>(categoryService.getOneCategory(createdCategory.getId()), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update category", description = "Update a category with optional file upload")
    public ResponseEntity<CategoryViewDTO> updateCategory(
        @PathVariable long id,
        @Parameter(
            description = "Category data as JSON string",
            schema = @Schema(implementation = CategoryCreateDTO.class)
        )
        @RequestPart(required = false) String data,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
       if (data == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }

        CategoryUpdateDTO updateDTO;
        try {
            updateDTO = new ObjectMapper().readValue(data, CategoryUpdateDTO.class);
        } catch (UnrecognizedPropertyException e) {
            throw new FieldDontExistException(REQUEST_INVALID_FIELD + e.getPropertyName());
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(REQUEST_INVALID_JSON);
        }

        // Update the category first (without file path)
        categoryService.updateCategory(id, updateDTO);

        if (file != null && !file.isEmpty()) {
            try {
                String bucket = minioService.getBucket();
                String filePath = minioService.uploadFileAndGetPath(file);
                String contentType = file.getContentType();
                updateDTO.setBucket(bucket);
                updateDTO.setObjectName(filePath);
                updateDTO.setObjectContentType(contentType);
                categoryService.updateCategory(id, updateDTO);
            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(categoryService.getOneCategory(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
