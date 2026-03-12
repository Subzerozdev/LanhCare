package com.lanhcare.dto.admin.deletionrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Admin Deletion Request list response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDeletionRequestResponse {
    
    private Integer id;
    private String email;
    private String reason;
    private String status;
    private String createdAt;
    private String processedAt;
}
