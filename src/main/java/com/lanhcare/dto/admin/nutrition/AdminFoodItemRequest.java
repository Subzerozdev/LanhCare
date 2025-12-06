package com.lanhcare.dto.admin.nutrition;

import com.lanhcare.entity.FoodItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating Food Item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodItemRequest {
    
    @NotBlank(message = "Food name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Calories is required")
    private BigDecimal calo;
    
    private String servingUnit;
    private BigDecimal standardServingSize;
    
    @NotNull(message = "Food type ID is required")
    private Integer foodTypeId;
    
    private String dataSource;
    private String imageUrl;
    
    @Builder.Default
    private FoodItemStatus status = FoodItemStatus.ACTIVE;
}
