package com.ytpor.api.service;

import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.ItemAttributeCreateDTO;
import com.ytpor.api.model.ItemAttributeUpdateDTO;
import com.ytpor.api.repository.ItemAttributeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemAttributeService {

    // Declare the repository as final to ensure its immutability
    private final ItemAttributeRepository itemAttributeRepository;

    // Use constructor-based dependency injection
    public ItemAttributeService(ItemAttributeRepository itemAttributeRepository) {
        this.itemAttributeRepository = itemAttributeRepository;
    }

    public Page<ItemAttribute> getAllItemAttributes(Pageable pageable) {
        return itemAttributeRepository.findAll(pageable);
    }

    public ItemAttribute getOneItemAttribute(long id) {
        return itemAttributeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Item attribute not found for id: " + id));
    }

    public ItemAttribute createItemAttribute(ItemAttributeCreateDTO createDTO) {
        try {
            ItemAttribute itemAttribute = new ItemAttribute();
            itemAttribute.setName(createDTO.getName());
            itemAttribute.setDescription(createDTO.getDescription());
            return itemAttributeRepository.save(itemAttribute);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("Name already in use. Please use a different name.");
        }
    }

    public ItemAttribute updateItemAttribute(long id, ItemAttributeUpdateDTO updateDTO) {
        try {
            return itemAttributeRepository.findById(id).map(itemAttribute -> {
                if (updateDTO.getName() != null) {
                    itemAttribute.setName(updateDTO.getName());
                }
                if (updateDTO.getDescription() != null) {
                    itemAttribute.setDescription(updateDTO.getDescription());
                }
                return itemAttributeRepository.save(itemAttribute);
            }).orElseThrow(() -> new RecordNotFoundException("Item attribute not found for id: " + id));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("Name already in use. Please use a different name.");
        }
    }

    public void deleteItemAttribute(long id) {
        if (!itemAttributeRepository.existsById(id)) {
            throw new RecordNotFoundException("Item attribute not found for id: " + id);
        }
        itemAttributeRepository.deleteById(id);
    }
}
