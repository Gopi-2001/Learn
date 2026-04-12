package com.ecommerce.project.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.model.Category;


@RestController
public class CategoryController {

    List<Category> categories = new ArrayList<>();

    @GetMapping("/api/public/categories")
    public List<Category> getAllCategories() {
        return categories;
    }

    @PostMapping("/api/admin/category")
    public String postCategory(@RequestBody Category category) {
        categories.add(category);
        return "Category added successfully";
    }
    
    
}
