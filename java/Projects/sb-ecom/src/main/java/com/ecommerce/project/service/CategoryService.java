package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.model.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category postCategory(Category category);
    String deleteCategory(Long id);
}
