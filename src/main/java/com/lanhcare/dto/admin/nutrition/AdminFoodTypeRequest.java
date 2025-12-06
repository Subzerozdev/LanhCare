package com.lanhcare.dto.admin.nutrition;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Food Type request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodTypeRequest {
    
    @NotBlank(message = "Food type name is required")
    private String name;
}
