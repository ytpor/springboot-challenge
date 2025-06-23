package com.ytpor.api.service;

import com.ytpor.api.entity.ItemAttribute;
import com.ytpor.api.exception.DuplicateRecordException;
import com.ytpor.api.exception.RecordNotFoundException;
import com.ytpor.api.model.ItemAttributeCreateDTO;
import com.ytpor.api.model.ItemAttributeUpdateDTO;
import com.ytpor.api.repository.ItemAttributeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ItemAttributeService {

    private static final String ITEM_ATTRIBUTE_NOT_FOUND = "Item attribute not found for id: {}";
    private static final String ITEM_ATTRIBUTE_NOT_FOUND_MESSAGE = "Item attribute not found for id: ";
    private static final String NAME_IN_USE = "Name already in use: {}";
    private static final String NAME_IN_USE_MESSAGE = "Name already in use. Please use a different name.";
    private static final Logger logger = LoggerFactory.getLogger(ItemAttributeService.class);

    private final ItemAttributeRepository itemAttributeRepository;

    public ItemAttributeService(ItemAttributeRepository itemAttributeRepository) {
        this.itemAttributeRepository = itemAttributeRepository;
    }

    public Page<ItemAttribute> getAllItemAttributes(Pageable pageable) {
        // Check if sort is empty and apply default sort
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }
        return itemAttributeRepository.findAll(pageable);
    }

    public ItemAttribute getOneItemAttribute(long id) {
        return itemAttributeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(ITEM_ATTRIBUTE_NOT_FOUND, id);
                    return new RecordNotFoundException(ITEM_ATTRIBUTE_NOT_FOUND_MESSAGE + id);
                });
    }

    public ItemAttribute createItemAttribute(ItemAttributeCreateDTO createDTO) {
        ItemAttribute itemAttribute = new ItemAttribute();
        itemAttribute.setName(createDTO.getName());
        itemAttribute.setDescription(createDTO.getDescription());

        try {
            return itemAttributeRepository.save(itemAttribute);
        } catch (DataIntegrityViolationException e) {
            logger.error(NAME_IN_USE, createDTO.getName());
            throw new DuplicateRecordException(NAME_IN_USE_MESSAGE);
        }
    }

    public ItemAttribute updateItemAttribute(long id, ItemAttributeUpdateDTO updateDTO) {
        ItemAttribute itemAttribute = itemAttributeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(ITEM_ATTRIBUTE_NOT_FOUND, id);
                    return new RecordNotFoundException(ITEM_ATTRIBUTE_NOT_FOUND_MESSAGE + id);
                });

        if (updateDTO.getName() != null) {
            itemAttribute.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            itemAttribute.setDescription(updateDTO.getDescription());
        }

        try {
            return itemAttributeRepository.save(itemAttribute);
        } catch (DataIntegrityViolationException e) {
            logger.error(NAME_IN_USE, updateDTO.getName());
            throw new DuplicateRecordException(NAME_IN_USE_MESSAGE);
        }
    }

    public void deleteItemAttribute(long id) {
        if (!itemAttributeRepository.existsById(id)) {
            logger.error(ITEM_ATTRIBUTE_NOT_FOUND, id);
            throw new RecordNotFoundException(ITEM_ATTRIBUTE_NOT_FOUND_MESSAGE + id);
        }
        itemAttributeRepository.deleteById(id);
    }
}
