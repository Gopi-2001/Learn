package in.gopikant.billingSoftware.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gopikant.billingSoftware.io.CategoryRequest;
import in.gopikant.billingSoftware.io.CategoryResponse;
import in.gopikant.billingSoftware.service.CategoryService;
import in.gopikant.billingSoftware.service.impl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
//@RequestMapping("/categories")
@RequiredArgsConstructor
//@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
//    public CategoryResponse addCategory(@RequestBody CategoryRequest request) {
//        return categoryService.add(request);
//    }
    public CategoryResponse addCategory(@RequestPart("category") String categoryString, @RequestPart("file") MultipartFile uploadFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest request = null;

        try {
           request = objectMapper.readValue(categoryString, CategoryRequest.class);
           return categoryService.add(request,uploadFile);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception occured while parsing the Json: " + e.getMessage());
        }

    }

    @GetMapping("/categories")
    public List<CategoryResponse> fetchCategories(){
        return categoryService.read();
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String categoryId){

        try {
            categoryService.delete(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
}


/**
 * @RestController is used to create RESTful web services in Spring Boot.
 * It combines @Controller and @ResponseBody, returning JSON or other responses directly.
 * It handles HTTP requests (GET, POST, PUT, DELETE).
 *
 * Example usage:
 * @RestController
 * @RequestMapping("/api")
 * public class UserController {
 *
 *     @GetMapping("/user")
 *     public String getUser() {
 *         return "Hello, User!";
 *     }
 * }
 *
 * Accessing /api/user will return "Hello, User!" as a response.
 */

/**
 * @RequiredArgsConstructor generates a constructor with required fields:
 * - Includes only final and @NonNull fields.
 * - Useful for dependency injection and immutable objects.
 *
 * Example usage:
 *
 * @RequiredArgsConstructor
 * public class User {
 *     private final String name;
 *     private final int age;
 *     private String address; // Not included in the constructor
 * }
 *
 * User user = new User("Alice", 25); // Only final fields are required
 */