package com.ecommerce.project.category;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;

@RestController
public class CategoryController {
    
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/api/admin/category")
    public String postCategory(@RequestBody Category category) {
        categoryService.postCategory(category);
        return "Category added successfully";
    }

    @DeleteMapping("/api/admin/category/{id}")
    public String deleteCategory(@PathVariable Long id) {
        String status = categoryService.deleteCategory(id);
        return status; 
    }
    
    
}
