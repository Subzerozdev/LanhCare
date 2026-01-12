package com.lanhcare.exception.exps;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends LanhCareException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
