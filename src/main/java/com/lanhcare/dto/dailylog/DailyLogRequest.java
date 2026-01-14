package com.lanhcare.dto.dailylog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyLogRequest {
    private LocalDate loggedDate;
    private Integer stepAmount;
    private Integer accountId;
}
