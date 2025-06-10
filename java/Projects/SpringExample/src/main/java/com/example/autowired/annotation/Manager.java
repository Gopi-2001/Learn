package com.example.autowired.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("manager1")
public class Manager {
    @Autowired // This can also be done with Constructor Injection as shown below but this is Field Inject
    @Qualifier("employee")
    private Employee employee; // Want to inject this dependency automatically without new() keyword

    // Spring will take a look at what this particular class needs i.e. employee.
    // It will automatically inject the bean of type employee
    // It is a constructor Injection
//   @Autowired
//   public Manager(Employee employee) {
//        this.employee = employee;
//   }

    @Override
    public String toString() {
        return "Manager{" +
                "employee=" + employee +
                '}';
    }

}
