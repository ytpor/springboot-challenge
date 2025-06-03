package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.service.CategoryService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest()
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryService.getAllCategories(any(Pageable.class))).thenReturn(categoryPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").value(1));

        verify(categoryService, times(1)).getAllCategories(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetOneCategory() throws Exception {
        when(categoryService.getOneCategory(1L)).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

        verify(categoryService, times(1)).getOneCategory(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetOneCategory_NotFound() throws Exception {
        when(categoryService.getOneCategory(anyLong())).thenThrow(new RecordNotFoundException("Category not found."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(categoryService, times(1)).getOneCategory(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateCategory() throws Exception {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Test Category");

        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).createCategory(any(CategoryCreateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateCategory_NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/category")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateCategory() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Test Category");

        when(categoryService.updateCategory(anyLong(), any(CategoryUpdateDTO.class))).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).updateCategory(anyLong(), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateCategory_NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateCategory_NotFound() throws Exception {
        when(categoryService.updateCategory(anyLong(), any(CategoryUpdateDTO.class))).thenThrow(new RecordNotFoundException("Category not found."));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(categoryService, times(1)).updateCategory(anyLong(), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteCategory_NotFound() throws Exception {
        doThrow(new RecordNotFoundException("Category not found.")).when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(categoryService, times(1)).deleteCategory(anyLong());
    }
}
