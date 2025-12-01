package com.lanhcare.exception;

/**
 * Exception thrown when a business validation fails
 */
public class ValidationException extends LanhCareException {
    
    public ValidationException(String message) {
        super(message);
    }
}
