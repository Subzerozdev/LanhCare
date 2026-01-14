package com.lanhcare.dto.dailylog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyLogResponse {
    private Integer id;
    private LocalDate loggedDate;
    private Integer stepAmount;
    private BigDecimal totalCaloriesIn;
    private BigDecimal totalCaloriesOut;
    private Integer accountId;
}