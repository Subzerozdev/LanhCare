package com.lanhcare.dto.admin.settings;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for System Setting request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSettingRequest {
    
    @NotBlank(message = "Setting value is required")
    private String value;
    
    private String description;
}
