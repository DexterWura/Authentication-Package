package com.authpackage.authentication.exception;

public class InvalidTokenException extends AuthenticationException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
}

