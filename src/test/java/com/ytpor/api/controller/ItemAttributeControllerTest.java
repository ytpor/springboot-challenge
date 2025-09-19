package com.ytpor.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.ItemAttributeCreateDTO;
import com.ytpor.api.model.ItemAttributeUpdateDTO;
import com.ytpor.api.service.ItemAttributeService;
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
class ItemAttributeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemAttributeService itemAttributeService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemAttribute itemAttribute;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        itemAttribute = new ItemAttribute();
        itemAttribute.setId(1L);
        itemAttribute.setName("Test ItemAttribute");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetAllItemAttributes() throws Exception {
        List<ItemAttribute> itemAttributes = Arrays.asList(new ItemAttribute(), new ItemAttribute());
        Page<ItemAttribute> itemAttributePage = new PageImpl<>(itemAttributes);

        when(itemAttributeService.getAllItemAttributes(any(Pageable.class))).thenReturn(itemAttributePage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/attribute")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").value(1));

        verify(itemAttributeService, times(1)).getAllItemAttributes(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetOneItemAttribute() throws Exception {
        when(itemAttributeService.getOneItemAttribute(1L)).thenReturn(itemAttribute);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));

        verify(itemAttributeService, times(1)).getOneItemAttribute(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetOneItemAttribute_NotFound() throws Exception {
        when(itemAttributeService.getOneItemAttribute(anyLong())).thenThrow(new RecordNotFoundException("Item Attribute not found."));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(itemAttributeService, times(1)).getOneItemAttribute(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateItemAttribute() throws Exception {
        ItemAttributeCreateDTO createDTO = new ItemAttributeCreateDTO();
        createDTO.setName("Test ItemAttribute");

        when(itemAttributeService.createItemAttribute(any(ItemAttributeCreateDTO.class))).thenReturn(itemAttribute);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/attribute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test ItemAttribute"));

        verify(itemAttributeService, times(1)).createItemAttribute(any(ItemAttributeCreateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateItemAttribute_NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/attribute")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateItemAttribute() throws Exception {
        ItemAttributeUpdateDTO updateDTO = new ItemAttributeUpdateDTO();
        updateDTO.setName("Test ItemAttribute");

        when(itemAttributeService.updateItemAttribute(anyLong(), any(ItemAttributeUpdateDTO.class))).thenReturn(itemAttribute);

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test ItemAttribute"));

        verify(itemAttributeService, times(1)).updateItemAttribute(anyLong(), any(ItemAttributeUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateItemAttribute_NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateItemAttribute_NotFound() throws Exception {
        when(itemAttributeService.updateItemAttribute(anyLong(), any(ItemAttributeUpdateDTO.class))).thenThrow(new RecordNotFoundException("Item Attribute not found."));

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/attribute/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemAttribute)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(itemAttributeService, times(1)).updateItemAttribute(anyLong(), any(ItemAttributeUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteItemAttribute() throws Exception {
        doNothing().when(itemAttributeService).deleteItemAttribute(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(itemAttributeService, times(1)).deleteItemAttribute(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteItemAttribute_NotFound() throws Exception {
        doThrow(new RecordNotFoundException("Item Attribute not found.")).when(itemAttributeService).deleteItemAttribute(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/attribute/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record Not Found"));

        verify(itemAttributeService, times(1)).deleteItemAttribute(anyLong());
    }
}
