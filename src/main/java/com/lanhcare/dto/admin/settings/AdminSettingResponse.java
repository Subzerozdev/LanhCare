package com.lanhcare.dto.admin.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for System Setting response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSettingResponse {
    
    private Integer id;
    private String key;
    private String value;
    private String description;
    private String updatedAt;
}
