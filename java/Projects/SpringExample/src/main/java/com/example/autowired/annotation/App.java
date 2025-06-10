package com.example.autowired.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args){

        // ApplicationContext is a interface and it has different implementation
        // ClassPathXmlApplicationContext() is one of the implementation
        // AnnotationConfigApplicationContext() is annotation based implementation
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Employee employee = (Employee) context.getBean("employee");

        Manager manager = context.getBean("manager1",Manager.class);

        System.out.println(employee.toString());
        System.out.println(manager.toString());

    }
}
