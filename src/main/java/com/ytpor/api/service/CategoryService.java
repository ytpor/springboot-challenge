package com.ytpor.api.service;

import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryListDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.model.CategoryViewDTO;
import com.ytpor.api.model.MessageSend;
import com.ytpor.api.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private static final String CATEGORY_NOT_FOUND = "Category not found for id: {}";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found for id: ";
    private static final String NAME_IN_USE = "Name already in use: {}";
    private static final String NAME_IN_USE_MESSAGE = "Name already in use. Please use a different name.";
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final MinioService minioService;

    public CategoryService(
            CategoryRepository categoryRepository,
            MinioService minioService) {
        this.categoryRepository = categoryRepository;
        this.minioService = minioService;
    }

    @Cacheable(value = "Category", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<CategoryListDTO> getAllCategories(Pageable pageable) {
        // Check if sort is empty and apply default sort
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return categoryRepository.findAll(pageable)
                .map(category -> CategoryListDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .createdAt(category.getCreatedAt())
                        .updatedAt(category.getUpdatedAt())
                        .build());
    }

    public CategoryViewDTO getOneCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(CATEGORY_NOT_FOUND, id);
                    return new RecordNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + id);
                });

        String signedUrl = null;
        try {
            String bucket = category.getBucket();
            String objectName = category.getObjectName();

            if (bucket != null && !bucket.isEmpty() && objectName != null && !objectName.isEmpty()) {
                signedUrl = minioService.generatePresignedUrl(bucket, objectName);
            }
        } catch (Exception e) {
            logger.error("Failed to generate signed URL for category ID {}: {}", id, e.getMessage());
        }

        return CategoryViewDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .objectName(category.getObjectName())
                .objectUrl(signedUrl)
                .objectContentType(category.getObjectContentType())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @CacheEvict(value = "Category", allEntries = true)
    public Category createCategory(CategoryCreateDTO createDTO) {
        Category category = new Category();
        category.setName(createDTO.getName());
        category.setDescription(createDTO.getDescription());

        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            logger.error(NAME_IN_USE, createDTO.getName());
            throw new DuplicateRecordException(NAME_IN_USE_MESSAGE);
        }
    }

    @CacheEvict(value = "Category", allEntries = true)
    @CachePut(value = "Category", key = "#id")
    public Category updateCategory(long id, CategoryUpdateDTO updateDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(CATEGORY_NOT_FOUND, id);
                    return new RecordNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + id);
                });

        if (updateDTO.getName() != null) {
            category.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            category.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getBucket() != null) {
            category.setBucket(updateDTO.getBucket());
        }
        if (updateDTO.getObjectName() != null) {
            category.setObjectName(updateDTO.getObjectName());
        }
        if (updateDTO.getObjectContentType() != null) {
            category.setObjectContentType(updateDTO.getObjectContentType());
        }

        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            logger.error(NAME_IN_USE, updateDTO.getName());
            throw new DuplicateRecordException(NAME_IN_USE_MESSAGE);
        }
    }

    @CacheEvict(value = "Category", allEntries = true)
    public void deleteCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            logger.error(CATEGORY_NOT_FOUND, id);
            throw new RecordNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + id);
        }
        categoryRepository.deleteById(id);
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kuala_Lumpur") // Run every minute
    public void performDailyTask() {
        logger.info("Performing daily task");
    }

    public void backgroundProcess(MessageSend message) {
        logger.info("Performing background process {}", message);
    }
}
