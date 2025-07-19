package in.gopikant.billingSoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Builder annotation automatically generates a Builder class for this object.
 * It allows creating instances using a flexible and readable builder pattern.
 * Avoids telescoping constructors (multiple constructors with different parameters).
 * Allows optional parameters without needing multiple constructors.
 * Example usage:
 * Person person = Person.builder()
 *                       .name("John")
 *                       .age(30)
 *                       .build();
 */

/**
 * @Data annotation automatically generates:
 * - Getters and Setters for all fields
 * - toString() method for easy printing
 * - equals() and hashCode() for object comparison
 * - A constructor for required fields
 *
 * Example usage:
 *
 * Employee emp = new Employee();
 * emp.setName("Alice");
 * emp.setSalary(50000);
 * System.out.println(emp); // Prints Employee(name=Alice, salary=50000)
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    private String name;
    private String description;
    private String bgColor;

}
