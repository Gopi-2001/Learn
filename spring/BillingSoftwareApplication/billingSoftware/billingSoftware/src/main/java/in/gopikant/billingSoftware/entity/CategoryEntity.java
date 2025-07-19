package in.gopikant.billingSoftware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity // To mark this Table as JPA entity
@Table(name="tbl_category") // To provide the name of the Table - map the CategoryEntity Class with tbl_category Table
@Builder // Builder Notation from lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {
    // Fields
    @Id // to make id as Primary Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Strategy to generate Id value
    private Long id;

    @Column(unique = true) //To make CategoryId as unique column
    private String categoryId; // To Store the unique categoryId

    @Column(unique = true)
    private String name; // name of the category

    private String description;
    private String bgColor;
    private String imgUrl;
    @CreationTimestamp
    @Column(updatable = false) //Whenever we update, then createdAt should not be updated
    private Timestamp createdAt; // CreatedAt and UpdatedAt for Auditing Purpose
    @UpdateTimestamp
    private Timestamp updatedAt;


}
