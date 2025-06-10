package com.example.componentscan.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//Purpose of this class is just to have the configuration
@Configuration // Specify that this class is configuration class and you can load configuration from here
@ComponentScan(basePackages = "com.example.componentscan.annotation")
public class AppConfig {
}
