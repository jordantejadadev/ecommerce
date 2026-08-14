package com.jordan.ecommerce.service;

import com.jordan.ecommerce.entity.Category;
import com.jordan.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
