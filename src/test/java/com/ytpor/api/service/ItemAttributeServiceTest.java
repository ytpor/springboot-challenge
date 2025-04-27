package com.ytpor.api.service;

import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.ItemAttributeCreateDTO;
import com.ytpor.api.model.ItemAttributeUpdateDTO;
import com.ytpor.api.repository.ItemAttributeRepository;
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

class ItemAttributeServiceTest {

    @Mock
    private ItemAttributeRepository itemAttributeRepository;

    @InjectMocks
    private ItemAttributeService itemAttributeService;

    private ItemAttribute itemAttribute;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        itemAttribute = new ItemAttribute();
        itemAttribute.setId(1L);
        itemAttribute.setName("Color");
        itemAttribute.setDescription("Color description");
    }

    @Test
    void testGetAllItemAttributes() {
        Page<ItemAttribute> page = new PageImpl<>(Collections.singletonList(itemAttribute));
        when(itemAttributeRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<ItemAttribute> result = itemAttributeService.getAllItemAttributes(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Color");
    }

    @Test
    void testGetOneItemAttribute_Found() {
        when(itemAttributeRepository.findById(1L)).thenReturn(Optional.of(itemAttribute));

        ItemAttribute result = itemAttributeService.getOneItemAttribute(1L);

        assertThat(result.getName()).isEqualTo("Color");
    }

    @Test
    void testGetOneItemAttribute_NotFound() {
        when(itemAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemAttributeService.getOneItemAttribute(1L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Item attribute not found");
    }

    @Test
    void testCreateItemAttribute_Success() {
        ItemAttributeCreateDTO createDTO = new ItemAttributeCreateDTO();
        createDTO.setName("Size");
        createDTO.setDescription("Size description");

        when(itemAttributeRepository.save(any(ItemAttribute.class))).thenReturn(itemAttribute);

        ItemAttribute result = itemAttributeService.createItemAttribute(createDTO);

        assertThat(result.getName()).isEqualTo("Color"); // because we mock the return value
    }

    @Test
    void testCreateItemAttribute_Duplicate() {
        ItemAttributeCreateDTO createDTO = new ItemAttributeCreateDTO();
        createDTO.setName("Size");

        when(itemAttributeRepository.save(any(ItemAttribute.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> itemAttributeService.createItemAttribute(createDTO))
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessageContaining("Name already in use");
    }

    @Test
    void testUpdateItemAttribute_Success() {
        ItemAttributeUpdateDTO updateDTO = new ItemAttributeUpdateDTO();
        updateDTO.setName("Updated Name");

        when(itemAttributeRepository.findById(1L)).thenReturn(Optional.of(itemAttribute));
        when(itemAttributeRepository.save(any(ItemAttribute.class))).thenReturn(itemAttribute);

        ItemAttribute result = itemAttributeService.updateItemAttribute(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(itemAttributeRepository, times(1)).save(any(ItemAttribute.class));
    }

    @Test
    void testUpdateItemAttribute_NotFound() {
        ItemAttributeUpdateDTO updateDTO = new ItemAttributeUpdateDTO();
        updateDTO.setName("Updated Name");

        when(itemAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemAttributeService.updateItemAttribute(1L, updateDTO))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Item attribute not found");
    }

    @Test
    void testUpdateItemAttribute_Duplicate() {
        ItemAttributeUpdateDTO updateDTO = new ItemAttributeUpdateDTO();
        updateDTO.setName("Updated Name");

        when(itemAttributeRepository.findById(1L)).thenReturn(Optional.of(itemAttribute));
        when(itemAttributeRepository.save(any(ItemAttribute.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> itemAttributeService.updateItemAttribute(1L, updateDTO))
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessageContaining("Name already in use");
    }

    @Test
    void testDeleteItemAttribute_Success() {
        when(itemAttributeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemAttributeRepository).deleteById(1L);

        itemAttributeService.deleteItemAttribute(1L);

        verify(itemAttributeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteItemAttribute_NotFound() {
        when(itemAttributeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> itemAttributeService.deleteItemAttribute(1L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Item attribute not found");
    }
}
