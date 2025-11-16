package com.authpackage.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    
    private Jwt jwt = new Jwt();
    private Email email = new Email();
    private Security security = new Security();
    private Cors cors = new Cors();
    private EmailVerification emailVerification = new EmailVerification();
    private PasswordReset passwordReset = new PasswordReset();
    private RefreshToken refreshToken = new RefreshToken();
    
    @Data
    public static class Jwt {
        private String secret = "your-256-bit-secret-key-change-this-in-production-environment";
        private Long expiration = 86400000L; // 24 hours
        private Long refreshExpiration = 604800000L; // 7 days
    }
    
    @Data
    public static class Email {
        private String from = "noreply@example.com";
        private String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=";
        private String resetPasswordUrl = "http://localhost:8080/api/auth/reset-password?token=";
    }
    
    @Data
    public static class Security {
        private String publicEndpoints = "/api/auth/**";
    }
    
    @Data
    public static class Cors {
        private String allowedOrigins = "*";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
    }
    
    @Data
    public static class EmailVerification {
        private Boolean enabled = true;
        private Integer expiryHours = 24;
    }
    
    @Data
    public static class PasswordReset {
        private Integer expiryHours = 1;
    }
    
    @Data
    public static class RefreshToken {
        private Integer expiryDays = 7;
    }
}

