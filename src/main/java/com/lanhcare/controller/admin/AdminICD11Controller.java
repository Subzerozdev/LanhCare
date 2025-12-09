package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.icd11.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.ICD11Status;
import com.lanhcare.entity.ReviewStatus;
import com.lanhcare.entity.TranslationStatus;
import com.lanhcare.service.admin.AdminICD11Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin ICD-11 Management Controller
 * Handles Chapters, Codes, and Translations
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/icd11")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - ICD-11 Management", description = "Admin APIs for managing ICD-11 chapters, codes, and translations")
public class AdminICD11Controller {
    
    private final AdminICD11Service icd11Service;
    
    public AdminICD11Controller(AdminICD11Service icd11Service) {
        this.icd11Service = icd11Service;
    }
    
    // ==================== CHAPTERS ====================
    
    /**
     * Get all chapters with pagination and filters
     */
    @GetMapping("/chapters")
    @Operation(summary = "Get all chapters", description = "Get paginated list of ICD-11 chapters with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminICD11ChapterResponse>>> getAllChapters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ICD11Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminICD11ChapterResponse> chapters = icd11Service.getAllChapters(search, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Chapters retrieved successfully", chapters));
    }
    
    /**
     * Get all active chapters (for dropdowns)
     */
    @GetMapping("/chapters/active")
    @Operation(summary = "Get all active chapters", description = "Get list of all active chapters for selection")
    public ResponseEntity<ApiResponse<List<AdminICD11ChapterResponse>>> getAllActiveChapters() {
        List<AdminICD11ChapterResponse> chapters = icd11Service.getAllActiveChapters();
        return ResponseEntity.ok(ApiResponse.success("Active chapters retrieved successfully", chapters));
    }
    
    /**
     * Get chapter by URI
     */
    @GetMapping("/chapters/{chapterUri}")
    @Operation(summary = "Get chapter detail", description = "Get detailed information about a chapter")
    public ResponseEntity<ApiResponse<AdminICD11ChapterResponse>> getChapterByUri(
            @PathVariable String chapterUri) {
        AdminICD11ChapterResponse chapter = icd11Service.getChapterByUri(chapterUri);
        return ResponseEntity.ok(ApiResponse.success("Chapter retrieved successfully", chapter));
    }
    
    /**
     * Create chapter
     */
    @PostMapping("/chapters")
    @Operation(summary = "Create chapter", description = "Create a new ICD-11 chapter")
    public ResponseEntity<ApiResponse<AdminICD11ChapterResponse>> createChapter(
            @Valid @RequestBody AdminICD11ChapterRequest request) {
        
        AdminICD11ChapterResponse chapter = icd11Service.createChapter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Chapter created successfully", chapter));
    }
    
    /**
     * Update chapter
     */
    @PutMapping("/chapters/{chapterUri}")
    @Operation(summary = "Update chapter", description = "Update chapter information")
    public ResponseEntity<ApiResponse<AdminICD11ChapterResponse>> updateChapter(
            @PathVariable String chapterUri,
            @Valid @RequestBody AdminICD11ChapterRequest request) {
        
        AdminICD11ChapterResponse chapter = icd11Service.updateChapter(chapterUri, request);
        return ResponseEntity.ok(ApiResponse.success("Chapter updated successfully", chapter));
    }
    
    /**
     * Update chapter status
     */
    @PatchMapping("/chapters/{chapterUri}/status")
    @Operation(summary = "Update chapter status", description = "Change chapter status")
    public ResponseEntity<ApiResponse<AdminICD11ChapterResponse>> updateChapterStatus(
            @PathVariable String chapterUri,
            @RequestParam ICD11Status status) {
        
        AdminICD11ChapterResponse chapter = icd11Service.updateChapterStatus(chapterUri, status);
        return ResponseEntity.ok(ApiResponse.success("Chapter status updated successfully", chapter));
    }
    
    /**
     * Delete chapter (soft delete)
     */
    @DeleteMapping("/chapters/{chapterUri}")
    @Operation(summary = "Delete chapter", description = "Soft delete a chapter (sets status to DEPRECATED)")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(@PathVariable String chapterUri) {
        icd11Service.deleteChapter(chapterUri);
        return ResponseEntity.ok(ApiResponse.success("Chapter deleted successfully", null));
    }
    
    // ==================== CODES ====================
    
    /**
     * Get all codes with pagination and filters
     */
    @GetMapping("/codes")
    @Operation(summary = "Get all codes", description = "Get paginated list of ICD-11 codes with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminICD11CodeResponse>>> getAllCodes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String chapterUri,
            @RequestParam(required = false) ICD11Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminICD11CodeResponse> codes = icd11Service.getAllCodes(search, chapterUri, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Codes retrieved successfully", codes));
    }
    
    /**
     * Get code by URI
     */
    @GetMapping("/codes/{icdUri}")
    @Operation(summary = "Get code detail", description = "Get detailed information about a code")
    public ResponseEntity<ApiResponse<AdminICD11CodeResponse>> getCodeByUri(@PathVariable String icdUri) {
        AdminICD11CodeResponse code = icd11Service.getCodeByUri(icdUri);
        return ResponseEntity.ok(ApiResponse.success("Code retrieved successfully", code));
    }
    
    /**
     * Get children of a code
     */
    @GetMapping("/codes/{icdUri}/children")
    @Operation(summary = "Get code children", description = "Get all child codes of a parent code")
    public ResponseEntity<ApiResponse<List<AdminICD11CodeResponse>>> getCodeChildren(
            @PathVariable String icdUri) {
        List<AdminICD11CodeResponse> children = icd11Service.getCodeChildren(icdUri);
        return ResponseEntity.ok(ApiResponse.success("Code children retrieved successfully", children));
    }
    
    /**
     * Create code
     */
    @PostMapping("/codes")
    @Operation(summary = "Create code", description = "Create a new ICD-11 code")
    public ResponseEntity<ApiResponse<AdminICD11CodeResponse>> createCode(
            @Valid @RequestBody AdminICD11CodeRequest request) {
        
        AdminICD11CodeResponse code = icd11Service.createCode(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Code created successfully", code));
    }
    
    /**
     * Update code
     */
    @PutMapping("/codes/{icdUri}")
    @Operation(summary = "Update code", description = "Update code information")
    public ResponseEntity<ApiResponse<AdminICD11CodeResponse>> updateCode(
            @PathVariable String icdUri,
            @Valid @RequestBody AdminICD11CodeRequest request) {
        
        AdminICD11CodeResponse code = icd11Service.updateCode(icdUri, request);
        return ResponseEntity.ok(ApiResponse.success("Code updated successfully", code));
    }
    
    /**
     * Update code status
     */
    @PatchMapping("/codes/{icdUri}/status")
    @Operation(summary = "Update code status", description = "Change code status")
    public ResponseEntity<ApiResponse<AdminICD11CodeResponse>> updateCodeStatus(
            @PathVariable String icdUri,
            @RequestParam ICD11Status status) {
        
        AdminICD11CodeResponse code = icd11Service.updateCodeStatus(icdUri, status);
        return ResponseEntity.ok(ApiResponse.success("Code status updated successfully", code));
    }
    
    /**
     * Delete code (soft delete)
     */
    @DeleteMapping("/codes/{icdUri}")
    @Operation(summary = "Delete code", description = "Soft delete a code (sets status to DEPRECATED)")
    public ResponseEntity<ApiResponse<Void>> deleteCode(@PathVariable String icdUri) {
        icd11Service.deleteCode(icdUri);
        return ResponseEntity.ok(ApiResponse.success("Code deleted successfully", null));
    }
    
    // ==================== TRANSLATIONS ====================
    
    /**
     * Get all translations with pagination and filters
     */
    @GetMapping("/translations")
    @Operation(summary = "Get all translations", description = "Get paginated list of translations with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminICD11TranslationResponse>>> getAllTranslations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TranslationStatus status,
            @RequestParam(required = false) ReviewStatus reviewStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminICD11TranslationResponse> translations = 
                icd11Service.getAllTranslations(search, status, reviewStatus, page, size);
        return ResponseEntity.ok(ApiResponse.success("Translations retrieved successfully", translations));
    }
    
    /**
     * Get translation by ID
     */
    @GetMapping("/translations/{id}")
    @Operation(summary = "Get translation detail", description = "Get detailed information about a translation")
    public ResponseEntity<ApiResponse<AdminICD11TranslationResponse>> getTranslationById(
            @PathVariable Integer id) {
        AdminICD11TranslationResponse translation = icd11Service.getTranslationById(id);
        return ResponseEntity.ok(ApiResponse.success("Translation retrieved successfully", translation));
    }
    
    /**
     * Get translations by ICD code
     */
    @GetMapping("/codes/{icdUri}/translations")
    @Operation(summary = "Get code translations", description = "Get all translations for a specific ICD code")
    public ResponseEntity<ApiResponse<List<AdminICD11TranslationResponse>>> getTranslationsByCode(
            @PathVariable String icdUri) {
        List<AdminICD11TranslationResponse> translations = icd11Service.getTranslationsByCode(icdUri);
        return ResponseEntity.ok(ApiResponse.success("Translations retrieved successfully", translations));
    }
    
    /**
     * Create translation
     */
    @PostMapping("/translations")
    @Operation(summary = "Create translation", description = "Create a new translation for an ICD code")
    public ResponseEntity<ApiResponse<AdminICD11TranslationResponse>> createTranslation(
            @Valid @RequestBody AdminICD11TranslationRequest request) {
        
        AdminICD11TranslationResponse translation = icd11Service.createTranslation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Translation created successfully", translation));
    }
    
    /**
     * Update translation
     */
    @PutMapping("/translations/{id}")
    @Operation(summary = "Update translation", description = "Update translation information")
    public ResponseEntity<ApiResponse<AdminICD11TranslationResponse>> updateTranslation(
            @PathVariable Integer id,
            @Valid @RequestBody AdminICD11TranslationRequest request) {
        
        AdminICD11TranslationResponse translation = icd11Service.updateTranslation(id, request);
        return ResponseEntity.ok(ApiResponse.success("Translation updated successfully", translation));
    }
    
    /**
     * Update translation review status
     */
    @PatchMapping("/translations/{id}/review")
    @Operation(summary = "Update review status", description = "Change translation review status (auto-publishes if APPROVED)")
    public ResponseEntity<ApiResponse<AdminICD11TranslationResponse>> updateTranslationReviewStatus(
            @PathVariable Integer id,
            @RequestParam ReviewStatus reviewStatus) {
        
        AdminICD11TranslationResponse translation = icd11Service.updateTranslationReviewStatus(id, reviewStatus);
        return ResponseEntity.ok(ApiResponse.success("Translation review status updated successfully", translation));
    }
    
    /**
     * Delete translation (soft delete)
     */
    @DeleteMapping("/translations/{id}")
    @Operation(summary = "Delete translation", description = "Soft delete a translation (sets status to ARCHIVED)")
    public ResponseEntity<ApiResponse<Void>> deleteTranslation(@PathVariable Integer id) {
        icd11Service.deleteTranslation(id);
        return ResponseEntity.ok(ApiResponse.success("Translation deleted successfully", null));
    }
}

