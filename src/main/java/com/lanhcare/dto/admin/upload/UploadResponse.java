package com.lanhcare.dto.admin.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for file upload operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    
    /**
     * Single URL (for single file upload)
     */
    private String url;
    
    /**
     * List of URLs (for multiple file upload)
     */
    private List<String> urls;
    
    /**
     * Type of uploaded file (IMAGE/VIDEO)
     */
    private String type;
    
    /**
     * Number of files uploaded
     */
    private int count;
    
    // Static factory methods
    public static UploadResponse single(String url, String type) {
        return UploadResponse.builder()
                .url(url)
                .type(type)
                .count(1)
                .build();
    }
    
    public static UploadResponse multiple(List<String> urls, String type) {
        return UploadResponse.builder()
                .urls(urls)
                .type(type)
                .count(urls.size())
                .build();
    }
}
