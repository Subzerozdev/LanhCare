package com.lanhcare.dto.meallog;

import com.lanhcare.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogRequest {
    private int mealLogId;
    private int accountId;
    private MealType mealType;
    private LocalDate mealDate;
    private String notes;
}
