package com.lanhcare.dto.meallog;

import com.lanhcare.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogRequest {
    private Integer dailyLogId;
    private MealType mealType;
    private LocalTime loggedTime;
    private String notes;
}
