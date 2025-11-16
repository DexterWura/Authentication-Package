package com.authpackage.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AuthenticationPackageApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationPackageApplication.class, args);
    }
}

