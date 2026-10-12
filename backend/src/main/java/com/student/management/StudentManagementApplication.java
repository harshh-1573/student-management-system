package com.student.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for the Student Management System Backend.
 * 
 * Annotations:
 * - @SpringBootApplication: Combines @Configuration, @EnableAutoConfiguration, 
 *   and @ComponentScan across the 'com.student.management' root package.
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
