package in.gopikant.billingSoftware.repository;

import in.gopikant.billingSoftware.entity.CategoryEntity;
import in.gopikant.billingSoftware.io.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    Optional<CategoryEntity> findByCategoryId(String categoryId);
}


/**
 * Optional<T> is used to handle nullable values safely in Java and Spring.
 * - Helps avoid NullPointerException (NPE).
 * - Used in Spring Data JPA to handle missing database records.
 * - Supports functional programming with map(), orElse(), and ifPresent().
 *
 * Example usage:
 *
 * Optional<String> name = Optional.ofNullable(getName());
 * System.out.println(name.orElse("Default Name")); // Returns value or default
 */
