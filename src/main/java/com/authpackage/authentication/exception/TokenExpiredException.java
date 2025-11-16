package com.authpackage.authentication.exception;

public class TokenExpiredException extends AuthenticationException {
    
    public TokenExpiredException(String message) {
        super(message);
    }
}

