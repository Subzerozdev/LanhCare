package com.lanhcare.dto.admin.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for rejecting a post
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRejectPostRequest {
    
    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
