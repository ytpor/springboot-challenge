package com.ytpor.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ytpor.api.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
