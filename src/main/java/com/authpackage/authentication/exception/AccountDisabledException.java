package com.authpackage.authentication.exception;

public class AccountDisabledException extends AuthenticationException {
    
    public AccountDisabledException(String message) {
        super(message);
    }
}

