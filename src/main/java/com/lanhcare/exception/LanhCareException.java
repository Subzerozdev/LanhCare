package com.lanhcare.exception;

/**
 * Base exception class for all custom exceptions in LanhCare application
 */
public class LanhCareException extends RuntimeException {
    
    public LanhCareException(String message) {
        super(message);
    }
    
    public LanhCareException(String message, Throwable cause) {
        super(message, cause);
    }
}
