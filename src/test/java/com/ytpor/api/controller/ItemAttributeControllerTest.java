package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.repository.ItemAttributeRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest()
class ItemAttributeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemAttributeRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllItemAttributes() throws Exception {
        List<ItemAttribute> itemAttributes = Arrays.asList(new ItemAttribute(), new ItemAttribute());
        Page<ItemAttribute> itemAttributePage = new PageImpl<>(itemAttributes);

        when(itemRepository.findAll(any(Pageable.class))).thenReturn(itemAttributePage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/attribute")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").value(1));

        verify(itemRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetItemAttributeById() throws Exception {
        ItemAttribute item = new ItemAttribute();
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemAttributeByIdNotFound() throws Exception {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void testCreateItemAttribute() throws Exception {
        ItemAttribute newItemAttribute = new ItemAttribute();
        newItemAttribute.setId(1L);
        newItemAttribute.setName("Test ItemAttribute");

        when(itemRepository.save(any(ItemAttribute.class))).thenReturn(newItemAttribute);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/attribute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItemAttribute)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test ItemAttribute"));

        verify(itemRepository, times(1)).save(any(ItemAttribute.class));
    }

    @Test
    void testCreateItemAttributeNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/attribute")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateItemAttribute() throws Exception {
        ItemAttribute newItemAttribute = new ItemAttribute();
        newItemAttribute.setId(1L);
        newItemAttribute.setName("Updated ItemAttribute");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(new ItemAttribute()));
        when(itemRepository.save(any(ItemAttribute.class))).thenReturn(newItemAttribute);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItemAttribute)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated ItemAttribute"));

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(ItemAttribute.class));
    }

    @Test
    void testUpdateItemAttributeNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testUpdateItemAttributeNotFound() throws Exception {
        ItemAttribute newItemAttribute = new ItemAttribute();
        newItemAttribute.setId(1L);
        newItemAttribute.setName("Updated ItemAttribute");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItemAttribute)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeleteItemAttribute() throws Exception {
        when(itemRepository.existsById(1L)).thenReturn(true);

        doNothing().when(itemRepository).deleteById(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(itemRepository, times(1)).existsById(1L);
    }

    @Test
    void testDeleteItemAttributeNotFound() throws Exception {
        when(itemRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(itemRepository, times(1)).existsById(1L);
    }
}
