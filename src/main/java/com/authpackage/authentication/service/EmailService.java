package com.authpackage.authentication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${auth.email.from:noreply@example.com}")
    private String fromEmail;
    
    @Value("${auth.email.verification-url:http://localhost:8080/api/auth/verify-email?token=}")
    private String verificationUrl;
    
    @Value("${auth.email.reset-password-url:http://localhost:8080/api/auth/reset-password?token=}")
    private String resetPasswordUrl;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verify Your Email Address");
        message.setText("Please click the following link to verify your email address:\n\n" +
            verificationUrl + token + "\n\n" +
            "This link will expire in 24 hours.");
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Reset Your Password");
        message.setText("Please click the following link to reset your password:\n\n" +
            resetPasswordUrl + token + "\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you did not request this, please ignore this email.");
        mailSender.send(message);
    }
}

