package com.lanhcare.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response Wrapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private int status;
    private String message;
    private T data;
    
    /**
     * Success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }
    
    /**
     * Success response with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Created response (201)
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * No content response (204)
     */
    public static <T> ApiResponse<T> noContent(String message) {
        return ApiResponse.<T>builder()
                .status(204)
                .message(message)
                .data(null)
                .build();
    }
}
