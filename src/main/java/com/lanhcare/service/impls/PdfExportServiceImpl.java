package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.HealthReportResponse;
import com.lanhcare.service.HealthReportService;
import com.lanhcare.service.PdfExportService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    private final HealthReportService healthReportService;

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(41, 128, 185));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(52, 73, 94));
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] exportHealthReport(Integer accountId, LocalDate from, LocalDate to) {
        HealthReportResponse report = healthReportService.getFullReport(accountId, from, to);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Paragraph title = new Paragraph("LanhCare - Bao Cao Suc Khoe", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Paragraph period = new Paragraph(
                    report.getStartDate().format(DATE_FMT) + " - " + report.getEndDate().format(DATE_FMT),
                    BODY_FONT);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Summary table
            document.add(new Paragraph("Tong Quan", LABEL_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{1, 1});

            addSummaryRow(summaryTable, "So ngay ghi log", String.valueOf(report.getDaysLogged()));
            addSummaryRow(summaryTable, "TB Calories nap/ngay", report.getAvgCaloriesIn() + " kcal");
            addSummaryRow(summaryTable, "TB Calories tieu hao/ngay", report.getAvgCaloriesOut() + " kcal");
            addSummaryRow(summaryTable, "Can bang calories", report.getCalorieBalance() + " kcal");
            addSummaryRow(summaryTable, "TB buoc chan/ngay", String.valueOf(report.getAvgSteps()));
            addSummaryRow(summaryTable, "Tong so bua an", String.valueOf(report.getTotalMeals()));
            addSummaryRow(summaryTable, "Tong so bai tap", String.valueOf(report.getTotalExercises()));

            document.add(summaryTable);
            document.add(Chunk.NEWLINE);

            // Health Profile
            if (report.getWeightKg() != null) {
                document.add(new Paragraph("Ho So Suc Khoe", LABEL_FONT));
                document.add(Chunk.NEWLINE);

                PdfPTable profileTable = new PdfPTable(2);
                profileTable.setWidthPercentage(100);
                profileTable.setWidths(new float[]{1, 1});

                addSummaryRow(profileTable, "Can nang", report.getWeightKg() + " kg");
                addSummaryRow(profileTable, "BMI", report.getBmiValue() != null ? report.getBmiValue().toString() : "N/A");
                addSummaryRow(profileTable, "Tinh trang BMI", report.getBmiStatus() != null ? report.getBmiStatus() : "N/A");
                addSummaryRow(profileTable, "Muc tieu", report.getHealthGoal() != null ? report.getHealthGoal() : "N/A");

                document.add(profileTable);
                document.add(Chunk.NEWLINE);
            }

            // Daily details
            if (report.getDailyDetails() != null && !report.getDailyDetails().isEmpty()) {
                document.add(new Paragraph("Chi Tiet Theo Ngay", LABEL_FONT));
                document.add(Chunk.NEWLINE);

                PdfPTable detailTable = new PdfPTable(5);
                detailTable.setWidthPercentage(100);
                detailTable.setWidths(new float[]{1.5f, 1.2f, 1.2f, 1, 1});

                addHeaderCell(detailTable, "Ngay");
                addHeaderCell(detailTable, "Cal Nap");
                addHeaderCell(detailTable, "Cal Tieu");
                addHeaderCell(detailTable, "Buoc");
                addHeaderCell(detailTable, "Bua/Tap");

                for (HealthReportResponse.DailyDetail d : report.getDailyDetails()) {
                    addCell(detailTable, d.getDate().format(DATE_FMT));
                    addCell(detailTable, d.getCaloriesIn().toString());
                    addCell(detailTable, d.getCaloriesOut().toString());
                    addCell(detailTable, String.valueOf(d.getSteps()));
                    addCell(detailTable, d.getMealCount() + "/" + d.getExerciseCount());
                }

                document.add(detailTable);
                document.add(Chunk.NEWLINE);
            }

            // Health tips
            if (report.getHealthTips() != null && !report.getHealthTips().isEmpty()) {
                document.add(new Paragraph("Loi Khuyen Suc Khoe", LABEL_FONT));
                document.add(Chunk.NEWLINE);

                com.lowagie.text.List tipList = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                for (String tip : report.getHealthTips()) {
                    tipList.add(new ListItem(tip, BODY_FONT));
                }
                document.add(tipList);
            }

            // Footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generated by LanhCare - " + LocalDate.now().format(DATE_FMT),
                    new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF report for account {}", accountId, e);
            throw new RuntimeException("Không thể tạo báo cáo PDF. Vui lòng thử lại.", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new Color(41, 128, 185));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setPadding(6);
        labelCell.setBorderColor(new Color(189, 195, 199));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BODY_FONT));
        valueCell.setPadding(6);
        valueCell.setBorderColor(new Color(189, 195, 199));
        table.addCell(valueCell);
    }
}
