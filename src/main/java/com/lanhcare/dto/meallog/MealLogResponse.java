package com.lanhcare.dto.meallog;

import com.lanhcare.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogResponse {
    private Integer id;
    private Integer dailyLogId;
    private MealType mealType;
    private String mealTypeName;
    private LocalDate mealDate;
    private LocalTime loggedTime;
    private BigDecimal totalCalories;
    private String notes;
    private LocalDateTime createdAt;
}
