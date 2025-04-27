package com.ytpor.api.service;

import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private static final String CATEGORY_NOT_FOUND = "Category not found for id: {}";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found for id: ";
    private static final String NAME_IN_USE = "Name already in use: {}";
    private static final String NAME_IN_USE_MESSAGE = "Name already in use. Please use a different name.";
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "Category", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Cacheable(value = "Category", key = "#id")
    public Category getOneCategory(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(CATEGORY_NOT_FOUND, id);
                    return new RecordNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + id);
                });
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
}
