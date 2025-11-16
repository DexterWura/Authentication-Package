package com.authpackage.authentication.exception;

import com.authpackage.authentication.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageResponse> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<MessageResponse> handleTokenExpired(TokenExpiredException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<MessageResponse> handleAccountDisabled(AccountDisabledException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<MessageResponse> handleAccountLocked(AccountLockedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(MessageResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(MessageResponse.error("Invalid credentials"));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponse> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(MessageResponse.error("Access denied"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(MessageResponse.error("An unexpected error occurred"));
    }
}

