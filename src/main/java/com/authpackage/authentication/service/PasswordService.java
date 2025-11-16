package com.authpackage.authentication.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }
    
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

