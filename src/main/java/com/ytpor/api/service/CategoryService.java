package com.ytpor.api.service;

import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    // Declare the repository as final to ensure its immutability
    private final CategoryRepository categoryRepository;

    // Use constructor-based dependency injection
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value="Category", key="#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Cacheable(value="Category", key="#id")
    public Category getOneCategory(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Category not found for id: " + id));
    }

    @CacheEvict(value="Category", allEntries=true)
    public Category createCategory(CategoryCreateDTO createDTO) {
        try {
            Category category = new Category();
            category.setName(createDTO.getName());
            category.setDescription(createDTO.getDescription());
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("Name already in use. Please use a different name.");
        }
    }

    @CacheEvict(value="Category", allEntries=true)
    @CachePut(value="Category", key="#id")
    public Category updateCategory(long id, CategoryUpdateDTO updateDTO) {
        try {
            return categoryRepository.findById(id).map(category -> {
                if (updateDTO.getName() != null) {
                    category.setName(updateDTO.getName());
                }
                if (updateDTO.getDescription() != null) {
                    category.setDescription(updateDTO.getDescription());
                }
                return categoryRepository.save(category);
            }).orElseThrow(() -> new RecordNotFoundException("Category not found for id: " + id));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("Name already in use. Please use a different name.");
        }
    }

    @CacheEvict(value="Category", allEntries=true)
    public void deleteCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RecordNotFoundException("Category not found for id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
