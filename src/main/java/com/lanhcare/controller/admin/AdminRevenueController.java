package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.revenue.AdminTransactionResponse;
import com.lanhcare.dto.admin.revenue.RevenueStatsResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.TransactionStatus;
import com.lanhcare.service.admin.AdminRevenueService;
import com.lanhcare.service.admin.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin Revenue & Transaction Controller
 */
@RestController
@RequestMapping("/api/admin/revenue")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Revenue & Transactions", description = "Admin APIs for revenue management and transaction tracking")
public class AdminRevenueController {
    
    private final AdminRevenueService revenueService;
    private final ExportService exportService;
    
    public AdminRevenueController(AdminRevenueService revenueService,
                                   ExportService exportService) {
        this.revenueService = revenueService;
        this.exportService = exportService;
    }
    
    /**
     * Get all transactions with filters
     */
    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions", description = "Get paginated list of transactions with filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminTransactionResponse>>> getAllTransactions(
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer servicePlanId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminTransactionResponse> transactions = revenueService.getAllTransactions(
                status, userId, servicePlanId, startDate, endDate, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }
    
    /**
     * Get transaction detail
     */
    @GetMapping("/transactions/{id}")
    @Operation(summary = "Get transaction detail", description = "Get detailed information about a transaction")
    public ResponseEntity<ApiResponse<AdminTransactionResponse>> getTransactionById(@PathVariable Integer id) {
        AdminTransactionResponse transaction = revenueService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
    }
    
    /**
     * Get revenue statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get revenue statistics", description = "Get comprehensive revenue statistics")
    public ResponseEntity<ApiResponse<RevenueStatsResponse>> getRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        RevenueStatsResponse stats = revenueService.getRevenueStatistics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }
    
    /**
     * Export transactions to CSV
     */
    @GetMapping("/export")
    @Operation(summary = "Export transactions", description = "Export transactions to CSV, Excel, or PDF")
    public ResponseEntity<byte[]> exportTransactions(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionStatus status) {
        
        List<AdminTransactionResponse> transactions = revenueService.getTransactionsForExport(
                startDate, endDate, status);
        
        byte[] exportData;
        String filename;
        MediaType mediaType;
        
        switch (format.toUpperCase()) {
            case "EXCEL":
                exportData = exportService.exportTransactionsToExcel(transactions);
                filename = "transactions_" + System.currentTimeMillis() + ".xlsx";
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                break;
            case "PDF":
                exportData = exportService.exportTransactionsToPdf(transactions);
                filename = "transactions_" + System.currentTimeMillis() + ".pdf";
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case "CSV":
            default:
                exportData = exportService.exportTransactionsToCsv(transactions);
                filename = "transactions_" + System.currentTimeMillis() + ".csv";
                mediaType = MediaType.parseMediaType("text/csv");
                break;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(exportData);
    }
}
