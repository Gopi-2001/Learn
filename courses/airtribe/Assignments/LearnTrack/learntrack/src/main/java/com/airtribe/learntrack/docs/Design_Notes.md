**Q. Why you used ArrayList instead of array?**

Ans:
I used ArrayList in the service classes such as StudentService, CourseService, and EnrollmentService to store collections of entities dynamically. ArrayList is better than a normal array here because the number of students, courses, or enrollments is not fixed in advance, and we may add or remove items while the program is running. It also provides convenient built-in methods like add(), remove(), and iteration, which makes the code simpler and easier to manage.

**Q. Where you used static members and why?**

Ans:
I used static members in the IdGenerator class to keep shared ID counters for students, courses, employees, and enrollments. These counters are shared across all objects, so every new entity gets a unique ID without needing separate instance variables. I also used a static method in EmailValidator so the email validation logic can be called directly without creating an object, and the main method in the UI is static because it serves as the program entry point.

**Q. Where you used inheritance and what you gained from it?**

Ans:
I used inheritance by making Student and Trainer extend the Person class. This allowed the common attributes and behavior such as id, firstName, lastName, email, and display logic to be defined once in Person and reused by both subclasses. By using inheritance, the code becomes cleaner, avoids duplication, and makes it easier to manage shared functionality. The subclasses can also override methods like getDisplayName() to provide their own behavior.