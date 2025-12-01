package com.lanhcare.exception;

/**
 * Exception thrown when a resource already exists (e.g., duplicate email)
 */
public class ResourceAlreadyExistsException extends LanhCareException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
