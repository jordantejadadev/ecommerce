package com.jordan.ecommerce.repository;

import com.jordan.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

}
