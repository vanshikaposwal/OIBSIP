package com.library.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Digital Library Management System (LibSphere)
 * Main Spring Boot Application Entry Point.
 * 
 * - @SpringBootApplication: Enables auto-configuration, component scanning, and configuration properties.
 * - @EnableScheduling: Enables background scheduled tasks (such as overdue book checks).
 */
@SpringBootApplication
@EnableScheduling
public class DigitalLibraryManagementApplication {

    public static void main(String[] args) {
        // Starts the Spring Boot application and embedded server (Tomcat on port 8080)
        SpringApplication.run(DigitalLibraryManagementApplication.class, args);
    }
}
