## Technical Reference Note: JPA @EmbeddedId vs @MapsId

## 1. Quick Definitions

* **@EmbeddedId**: Used to build composite primary keys (keys made of multiple columns) using a reusable placeholder class.
* **@MapsId**: Used to build derived primary keys in a 1:1 relationship, forcing the child table to share the exact same ID column value as its parent. [2, 3, 4, 5]

------------------------------
## 2. @EmbeddedId (Composite Primary Keys) [6, 7, 8] ## Core Usage
Apply this when a database table does not use a single auto-incrementing integer as its key, but instead relies on a combination of two or more columns to guarantee row uniqueness. [9, 10]
## Implementation Requirements

1. Create a separate `PK` component class and mark it with `@Embeddable`.
2. The key class must implement Serializable.
3. The key class must override `equals()` and `hashCode()`.
4. Reference it inside the primary Entity using `@EmbeddedId`. [11, 12, 13, 14]

## Code Pattern
```
@Embeddablepublic class OrderItemId implements Serializable {
private Long orderId;
private Long productId;

    public OrderItemId() {} // Required
    // Must implement equals() and hashCode()
}

@Entitypublic class OrderItem {
@EmbeddedId
private OrderItemId id; // Composite Primary Key

    private Integer quantity;
}
```
------------------------------
## 3. @MapsId (Derived Primary Keys) [15] 

## Core Usage
Apply this in a strict 1:1 relationship where you want to eliminate the overhead of an independent identity sequence in the child table. The parent's Primary Key value is duplicated and utilized as both the Primary Key and Foreign Key of the child table. [16, 17]
## Implementation Requirements

1. Define a standard identifier field in the child entity using @Id without any @GeneratedValue configuration.
2. Add @MapsId directly onto the @OneToOne association field. [18]

## Code Pattern
```
@Entitypublic class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
}

@Entitypublic class UserProfile {
@Id
private Long id; // Shared Key value copy destination

    @OneToOne
    @MapsId // Syncs id field value directly with parent's ID
    @JoinColumn(name = "user_id") 
    private User user;
}
```
------------------------------
## 4. Cheat-Sheet Comparison

| Criteria [19, 20, 21, 22, 23] | @EmbeddedId | @MapsId |
|---|---|---|
| Primary Goal | Bundles multiple columns into a composite identifier. | Pushes a parent ID into a child entity to merge PK and FK. |
| Helper Class Needed? | Yes (Requires an explicit @Embeddable key object). | No (Operates directly on standard primitive/wrapper types). |
| Sequence Generator | Disabled (Values assigned explicitly by your code). | Disabled (Value derived automatically from parent lifecycle). |
| Main Use Case | Junction tables, historical tracking, multi-tenant schemas. | Lean 1:1 entity separations (Profiles, Settings, Audits). |

------------------------------

[1] [https://medium.com](https://medium.com/@vsreee256/mapping-relationships-with-composite-keys-in-jpa-571edd10bb04)
[2] [https://medium.com](https://medium.com/jpa-java-persistence-api-guide/hibernate-composite-primary-keys-with-embeddedid-annotation-46a8171e8d22)
[3] [https://thorben-janssen.com](https://thorben-janssen.com/composite-primary-keys-sequence/)
[4] [https://www.youtube.com](https://www.youtube.com/watch?v=inVpLlrjLvA)
[5] [https://medium.com](https://medium.com/@saurabh.kundu/hibernate-a-guide-to-essential-annotations-5588d459b6f)
[6] [https://medium.com](https://medium.com/jpa-java-persistence-api-guide/hibernate-composite-primary-keys-with-embeddedid-annotation-46a8171e8d22)
[7] [https://thorben-janssen.com](https://thorben-janssen.com/composite-primary-keys-sequence/)
[8] [https://javatechonline.com](https://javatechonline.com/mcq-on-spring-and-hibernate/)
[9] [https://www.govtcollegesundargarh.ac.in](https://www.govtcollegesundargarh.ac.in/uploads/images/notice/all_keys.pdf)
[10] [https://www.linkedin.com](https://www.linkedin.com/pulse/instagram-user-analytics-unveiling-insights-sql-pintu-kumar-kushwaha)
[11] [https://wiki.eclipse.org](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Entities/Ids/EmbeddedId)
[12] [https://vivekbansal.substack.com](https://vivekbansal.substack.com/p/java-hashmap-internals)
[13] [https://docs.redhat.com](https://docs.redhat.com/en/documentation/red_hat_jboss_web_server/2/html/hibernate_core_guide/chap-basic_or_mapping)
[14] [https://thorben-janssen.com](https://thorben-janssen.com/hibernate-tip-bidirectional-one-to-one-with-shared-composite-primary-key/)
[15] [https://jcs.ep.jhu.edu](https://jcs.ep.jhu.edu/ejava-javaee/coursedocs/content/html/jpa-relationex-one2one.html)
[16] [https://jcs.ep.jhu.edu](https://jcs.ep.jhu.edu/ejava-javaee/coursedocs/content/html/jpa-relationex-one2one.html)
[17] [https://jcs.ep.jhu.edu](https://jcs.ep.jhu.edu/ejava-javaee/coursedocs/content/html/jpa-relationex-one2one.html)
[18] [https://bojanstipic.com](https://bojanstipic.com/blog/jpa-pitfalls-generating-ids/)
[19] [https://medium.com](https://medium.com/jpa-java-persistence-api-guide/hibernate-composite-primary-keys-with-embeddedid-annotation-46a8171e8d22)
[20] [https://dev.to](https://dev.to/ankitdevcode/jpa-mapping-with-hibernate-one-to-one-relationship-g41)
[21] [https://thorben-janssen.com](https://thorben-janssen.com/composite-primary-keys-sequence/)
[22] [https://www.codingshuttle.com](https://www.codingshuttle.com/blogs/rdbms-guide-the-only-cheat-sheet-you-need-for-interviews-and-real-world-systems/)
[23] [https://docs.ecognition.com](https://docs.ecognition.com/v9.5.0/Resources/Images/Tutorial%203%20-%20Working%20with%20Maps.pdf)
