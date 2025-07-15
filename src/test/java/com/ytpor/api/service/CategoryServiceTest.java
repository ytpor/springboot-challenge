package com.ytpor.api.service;

import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryListDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.model.CategoryViewDTO;
import com.ytpor.api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronics Category");
    }

    @Test
    void testGetAllCategories() {
        Page<Category> page = new PageImpl<>(Collections.singletonList(category));
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CategoryListDTO> result = categoryService.getAllCategories(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void testGetOneCategory_Found() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryViewDTO result = categoryService.getOneCategory(1L);

        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void testGetOneCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getOneCategory(1L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void testCreateCategory_Success() {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Books");
        createDTO.setDescription("Books Category");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(createDTO);

        assertThat(result.getName()).isEqualTo("Books"); // because we mock it
    }

    @Test
    void testCreateCategory_Duplicate() {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Books");

        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> categoryService.createCategory(createDTO))
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessageContaining("Name already in use");
    }

    @Test
    void testUpdateCategory_Success() {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.updateCategory(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_NotFound() {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateDTO))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void testUpdateCategory_Duplicate() {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateDTO))
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessageContaining("Name already in use");
    }

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Category not found");
    }
}
