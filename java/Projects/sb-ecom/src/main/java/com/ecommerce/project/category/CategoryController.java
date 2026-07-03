package com.ecommerce.project.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;



@RestController
@RequestMapping("/api")
public class CategoryController {
    
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //@GetMapping("/api/public/categories")
    @RequestMapping(value = "/public/categories", method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    //@PostMapping("/api/admin/category")
    @RequestMapping(value = "/admin/category", method = RequestMethod.POST)
    public String requestMethodName(@RequestParam String param) {
        return new String();
    }
    
    public ResponseEntity<String> postCategory(@RequestBody Category category) {
        categoryService.postCategory(category);
    return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/category/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
        String status = categoryService.deleteCategory(id);
        
        //return new ResponseEntity<>(status, HttpStatus.OK);  //Method-1
        //return ResponseEntity.ok(status); //Method-2
        return ResponseEntity.status(HttpStatus.OK).body(status); //Method-3

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode()); 
        }
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, 
                                                 @RequestBody Category category) {

        try {
            Category savedCategory = categoryService.updateCategory(category,categoryId);

            return new ResponseEntity<>("Category with categoryId: " + categoryId + " updated successfully", HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode()); 
        }
    }
    
    
}
