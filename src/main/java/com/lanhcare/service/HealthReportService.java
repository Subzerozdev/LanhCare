package com.lanhcare.service;

import com.lanhcare.dto.subscription.HealthReportResponse;

import java.time.LocalDate;

public interface HealthReportService {
    /**
     * Weekly report: last 7 days summary (Basic plan+)
     */
    HealthReportResponse getWeeklyReport(Integer accountId);

    /**
     * Full report: custom date range with daily details + health tips (Premium plan)
     */
    HealthReportResponse getFullReport(Integer accountId, LocalDate from, LocalDate to);
}
