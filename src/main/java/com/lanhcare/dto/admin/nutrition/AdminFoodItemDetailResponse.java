package com.lanhcare.dto.admin.nutrition;

import com.lanhcare.entity.FoodItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for detailed Food Item response (includes nutrients)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodItemDetailResponse {
    
    private Integer id;
    private String name;
    private String description;
    private BigDecimal calo;
    private String servingUnit;
    private BigDecimal standardServingSize;
    private FoodItemStatus status;
    private String dataSource;
    private String imageUrl;
    
    // Food Type info
    private Integer foodTypeId;
    private String foodTypeName;
    
    // Nutrients
    private List<AdminFoodNutrientResponse> nutrients;
    private Integer nutrientCount;
    
    // Statistics
    private Long mealLogCount;
}

