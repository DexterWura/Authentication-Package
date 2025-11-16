package com.authpackage.authentication.exception;

public class UserNotFoundException extends AuthenticationException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
}

