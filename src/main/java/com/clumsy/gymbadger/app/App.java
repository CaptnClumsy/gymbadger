package com.clumsy.gymbadger.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
 
@SpringBootApplication
@ComponentScan(basePackages = "com.clumsy.gymbadger")
@EnableJpaRepositories(basePackages = "com.clumsy.gymbadger.repos")
@EntityScan(basePackages = "com.clumsy.gymbadger.entities")
public class App {
 
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
