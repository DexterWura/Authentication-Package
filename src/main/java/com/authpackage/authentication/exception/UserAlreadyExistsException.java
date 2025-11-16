package com.authpackage.authentication.exception;

public class UserAlreadyExistsException extends AuthenticationException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

