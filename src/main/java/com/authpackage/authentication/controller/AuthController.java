package com.authpackage.authentication.controller;

import com.authpackage.authentication.dto.request.*;
import com.authpackage.authentication.dto.response.AuthResponse;
import com.authpackage.authentication.dto.response.MessageResponse;
import com.authpackage.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authenticationService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authenticationService.refreshToken(request, httpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        authenticationService.verifyEmail(request);
        return ResponseEntity.ok(MessageResponse.success("Email verified successfully"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(MessageResponse.success(
            "If an account exists with this email, a password reset link has been sent"));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(MessageResponse.success("Password reset successfully"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody RefreshTokenRequest request) {
        authenticationService.logout(request.getRefreshToken());
        return ResponseEntity.ok(MessageResponse.success("Logged out successfully"));
    }
}

