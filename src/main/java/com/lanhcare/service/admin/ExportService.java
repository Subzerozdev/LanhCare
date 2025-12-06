package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.revenue.AdminTransactionResponse;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * Export Service for CSV, Excel, and PDF exports
 */
@Service
public class ExportService {
    
    /**
     * Export transactions to CSV
     */
    public byte[] exportTransactionsToCsv(List<AdminTransactionResponse> transactions) {
        try (StringWriter sw = new StringWriter();
             CSVWriter writer = new CSVWriter(sw)) {
            
            // Write header
            String[] header = {"ID", "User Email", "User Name", "Service Plan", "Amount", "Payment Method", "Status", "Transaction Date"};
            writer.writeNext(header);
            
            // Write data
            for (AdminTransactionResponse transaction : transactions) {
                String[] data = {
                    transaction.getId().toString(),
                    transaction.getUserEmail(),
                    transaction.getUserName(),
                    transaction.getServicePlanName(),
                    transaction.getAmount().toString(),
                    transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "",
                    transaction.getStatus(),
                    transaction.getTransactionDate()
                };
                writer.writeNext(data);
            }
            
            writer.flush();
            return sw.toString().getBytes();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }
    
    /**
     * Export transactions to Excel
     */
    public byte[] exportTransactionsToExcel(List<AdminTransactionResponse> transactions) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Transactions");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "User Email", "User Name", "Service Plan", "Amount", "Payment Method", "Status", "Transaction Date"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Create data rows
            int rowNum = 1;
            for (AdminTransactionResponse transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getUserEmail());
                row.createCell(2).setCellValue(transaction.getUserName());
                row.createCell(3).setCellValue(transaction.getServicePlanName());
                row.createCell(4).setCellValue(transaction.getAmount().doubleValue());
                row.createCell(5).setCellValue(transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "");
                row.createCell(6).setCellValue(transaction.getStatus());
                row.createCell(7).setCellValue(transaction.getTransactionDate());
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }
    
    /**
     * Export transactions to PDF (simplified HTML-based approach)
     */
    public byte[] exportTransactionsToPdf(List<AdminTransactionResponse> transactions) {
        // Simple HTML-based PDF using browser print functionality
        // For production, consider using iText or Apache PDFBox
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("table { border-collapse: collapse; width: 100%; }");
        html.append("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append("</style></head><body>");
        html.append("<h1>Transaction Report</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>ID</th><th>User Email</th><th>User Name</th><th>Service Plan</th>");
        html.append("<th>Amount</th><th>Payment Method</th><th>Status</th><th>Date</th>");
        html.append("</tr>");
        
        for (AdminTransactionResponse transaction : transactions) {
            html.append("<tr>");
            html.append("<td>").append(transaction.getId()).append("</td>");
            html.append("<td>").append(transaction.getUserEmail()).append("</td>");
            html.append("<td>").append(transaction.getUserName()).append("</td>");
            html.append("<td>").append(transaction.getServicePlanName()).append("</td>");
            html.append("<td>").append(transaction.getAmount()).append("</td>");
            html.append("<td>").append(transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "").append("</td>");
            html.append("<td>").append(transaction.getStatus()).append("</td>");
            html.append("<td>").append(transaction.getTransactionDate()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table></body></html>");
        
        return html.toString().getBytes();
    }
}
