package com.lanhcare.dto.admin.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for rejecting a comment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRejectCommentRequest {
    
    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
