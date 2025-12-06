package com.lanhcare.dto.admin.nutrition;

import com.lanhcare.entity.FoodItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Food Item response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodItemResponse {
    
    private Integer id;
    private String name;
    private String description;
    private BigDecimal calo;
    private String servingUnit;
    private BigDecimal standardServingSize;
    private FoodItemStatus status;
    private String foodTypeName;
    private String imageUrl;
    private Integer nutrientCount;
}
