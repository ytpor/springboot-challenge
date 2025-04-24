package com.ytpor.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ytpor.api.entity.ItemAttribute;

public interface ItemAttributeRepository extends JpaRepository<ItemAttribute, Long> {
}
