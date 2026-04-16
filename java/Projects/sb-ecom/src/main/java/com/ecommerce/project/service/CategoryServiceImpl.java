/**
 * The {@code @Service} annotation is a specialization of the {@link org.springframework.stereotype.Component}
 * annotation in the Spring Framework.
 * 
 * <p>It indicates that the annotated class is a "Service", originally defined by Domain-Driven Design
 * (DDD) as "an operation offered as an interface that stands alone in the model, with no encapsulated
 * state."</p>
 * 
 * <p><b>How it works in Spring Boot:</b></p>
 * <ul>
 *   <li><b>Component Scanning:</b> During application startup, Spring scans the package structure
 *       (typically from the main application class's package and subpackages) for classes annotated
 *       with {@code @Service}, {@code @Component}, or {@code @Repository}.</li>
 *   <li><b>Bean Registration:</b> Spring automatically registers these classes as Spring beans
 *       in the application context, meaning Spring will manage their lifecycle (creation, destruction).</li>
 *   <li><b>Dependency Injection:</b> The {@code @Service} bean can be injected into other components
 *       (controllers, other services, etc.) using {@code @Autowired}, constructor injection, or
 *       setter injection.</li>
 *   <li><b>Semantic Distinction:</b> While functionally identical to {@code @Component}, using
 *       {@code @Service} provides semantic clarity, indicating that the class contains business
 *       logic or service layer operations.</li>
 * </ul>
 * 
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * @Service
 * public class CategoryServiceImpl implements CategoryService {
 *     // Spring automatically creates and manages this bean
 * }
 * }</pre>
 * 
 * <p><b>Note:</b> Classes annotated with {@code @Service} are singleton by default (one instance
 * per Spring container). For stateful services, careful design or prototype scope may be needed.</p>
 * 
 * @see org.springframework.stereotype.Service
 * @see org.springframework.stereotype.Component
 * @see org.springframework.context.annotation.Bean
 */
/**
 * Implementation of {@link CategoryService} that provides business logic
 * for managing product categories.
 * 
 * <p>This service handles CRUD operations for categories, storing them
 * in an in-memory list. It is annotated with {@code @Service} to be
 * automatically detected and registered as a Spring bean.</p>
 * 
 * <p><b>Note:</b> This implementation uses an in-memory {@link ArrayList}
 * for storage, meaning data will be lost upon application restart.
 * For production, consider using a persistent data store.</p>
 *
 * @see Category
 * @see CategoryService
 */
package com.ecommerce.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.project.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    private Long categoryIdCounter = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public Category postCategory(Category category) {
        category.setCategoryId(categoryIdCounter++);
        categories.add(category);
        return category;
    }

    @Override
    public String deleteCategory(Long id) {
        Category category = categories.stream()
            .filter(cat -> cat.getCategoryId().equals(id))
            .findFirst()
            .get();

        categories.remove(category);
        return "Category with categoryId" + id + "deleted successfully !";
    }
    
}
    
