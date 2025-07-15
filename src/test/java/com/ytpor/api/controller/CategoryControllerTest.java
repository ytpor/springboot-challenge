package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.entity.Category;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.CategoryCreateDTO;
import com.ytpor.api.model.CategoryListDTO;
import com.ytpor.api.model.CategoryUpdateDTO;
import com.ytpor.api.model.CategoryViewDTO;
import com.ytpor.api.service.CategoryService;
import com.ytpor.api.service.MinioService;

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

    @MockitoBean
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;

    private CategoryViewDTO categoryView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        categoryView = CategoryViewDTO.builder()
                .id(1L)
                .name("Test Category")
                .objectName("uploaded/path/file.jpg")
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testGetAllCategories() throws Exception {
        CategoryListDTO dto1 = CategoryListDTO.builder().id(1L).name("Cat1").build();
        CategoryListDTO dto2 = CategoryListDTO.builder().id(2L).name("Cat2").build();
        List<CategoryListDTO> categories = Arrays.asList(dto1, dto2);
        Page<CategoryListDTO> categoryPage = new PageImpl<>(categories);

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
    @WithMockUser(username = "user", roles = { "USER" })
    void testGetOneCategory() throws Exception {
        when(categoryService.getOneCategory(1L)).thenReturn(categoryView);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

        verify(categoryService, times(1)).getOneCategory(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testGetOneCategory_NotFound() throws Exception {
        when(categoryService.getOneCategory(anyLong())).thenThrow(new RecordNotFoundException("Category not found."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(categoryService, times(1)).getOneCategory(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_MultipartDataOnly() throws Exception {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Test Category");

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(createDTO)))
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).createCategory(any(CategoryCreateDTO.class));
        verifyNoInteractions(minioService);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_MultipartDataAndFile() throws Exception {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Test Category");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Test Category");
        updatedCategory.setBucket("test-bucket");
        updatedCategory.setObjectName("uploaded/path/file.jpg");

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(category);
        when(minioService.uploadFileAndGetPath(any())).thenReturn("uploaded/path/file.jpg");
        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDTO.class))).thenReturn(updatedCategory);

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                "file.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(createDTO)))
                .file(file)
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("uploaded/path/file.jpg"));

        verify(categoryService, times(1)).createCategory(any(CategoryCreateDTO.class));
        verify(minioService, times(1)).uploadFileAndGetPath(any());
        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_UnsupportedMediaType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/category")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_MissingData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category")
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_FileUploadFailure() throws Exception {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Test Category");

        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(category);
        when(minioService.uploadFileAndGetPath(any())).thenThrow(new RuntimeException("upload failed"));

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                "file.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(createDTO)))
                .file(file)
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(categoryService, times(1)).createCategory(any(CategoryCreateDTO.class));
        verify(minioService, times(1)).uploadFileAndGetPath(any());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testCreateCategory_EmptyFile() throws Exception {
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Test Category");

        org.springframework.mock.web.MockMultipartFile emptyFile = new org.springframework.mock.web.MockMultipartFile(
                "file", "file.jpg", "image/jpeg", new byte[0]);

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.createCategory(any(CategoryCreateDTO.class))).thenReturn(category);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(createDTO)))
                .file(emptyFile)
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).createCategory(any(CategoryCreateDTO.class));
        verifyNoInteractions(minioService);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_MultipartDataOnly() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Category");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDTO.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(updateDTO)))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
        verifyNoInteractions(minioService);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_MultipartDataAndFile() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Category");

        Category updatedCategoryWithFile = new Category();
        updatedCategoryWithFile.setId(1L);
        updatedCategoryWithFile.setName("Updated Category");
        updatedCategoryWithFile.setObjectName("updated/path/file.jpg");

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDTO.class))).thenReturn(updatedCategoryWithFile);
        when(minioService.uploadFileAndGetPath(any())).thenReturn("uploaded/path/file.jpg");

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                "file.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(updateDTO)))
                .file(file)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("uploaded/path/file.jpg"));

        verify(minioService, times(1)).uploadFileAndGetPath(any());
        verify(categoryService, atLeastOnce()).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_MissingData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_FileUploadFailure() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Category");

        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDTO.class))).thenReturn(category);
        when(minioService.uploadFileAndGetPath(any())).thenThrow(new RuntimeException("upload failed"));

        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile("file",
                "file.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(updateDTO)))
                .file(file)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(minioService, times(1)).uploadFileAndGetPath(any());
        verify(categoryService, atLeastOnce()).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_EmptyFile() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Category");

        org.springframework.mock.web.MockMultipartFile emptyFile = new org.springframework.mock.web.MockMultipartFile(
                "file", "file.jpg", "image/jpeg", new byte[0]);

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");

        when(categoryService.getOneCategory(eq(1L))).thenReturn(categoryView);
        when(categoryService.updateCategory(eq(1L), any(CategoryUpdateDTO.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(updateDTO)))
                .file(emptyFile)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Category"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
        verifyNoInteractions(minioService);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_UnsupportedMediaType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testUpdateCategory_NotFound() throws Exception {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setName("Updated Category");

        when(categoryService.updateCategory(anyLong(), any(CategoryUpdateDTO.class)))
                .thenThrow(new RecordNotFoundException("Category not found."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/category/1")
                .file(new org.springframework.mock.web.MockMultipartFile("data", "", "application/json",
                        objectMapper.writeValueAsBytes(updateDTO)))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                })
                .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryUpdateDTO.class));
        verifyNoInteractions(minioService);
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void testDeleteCategory_NotFound() throws Exception {
        doThrow(new RecordNotFoundException("Category not found.")).when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/category/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(categoryService, times(1)).deleteCategory(anyLong());
    }
}
