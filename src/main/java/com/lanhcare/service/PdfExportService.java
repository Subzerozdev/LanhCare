package com.lanhcare.service;

import com.lanhcare.dto.subscription.HealthReportResponse;

public interface PdfExportService {
    /**
     * Export health report as PDF bytes
     */
    byte[] exportHealthReport(Integer accountId, java.time.LocalDate from, java.time.LocalDate to);
}
