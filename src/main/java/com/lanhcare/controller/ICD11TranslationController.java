package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.icd.ICD11TranslationResponse;
import com.lanhcare.entity.ICD11Translation;
import com.lanhcare.service.ICD11TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/icd-translations")
@RequiredArgsConstructor
@Tag(name = "ICD-11 Translation", description = "APIs for searching and managing ICD-11 translations")
public class ICD11TranslationController {

    private final ICD11TranslationService translationService;

    @GetMapping("/search")
    @Operation(
            summary = "Search ICD-11 translations",
            description = "Search for translations by keyword in title (vnTitle) or definition (vnDefinition)"
    )
    public ResponseEntity<ApiResponse<List<ICD11TranslationResponse>>> search(
            @Parameter(description = "Keyword to search in title or definition", example = "Sốt xuất huyết")
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        List<ICD11TranslationResponse> results = translationService
                .searchByKeyword(keyword).stream()
                .map(translationService::mapToResponse)
                .toList();

        String message = results.isEmpty()
                ? "No translations found for the given keyword"
                : "Translations retrieved successfully";

        return ResponseEntity.ok(ApiResponse.success(message, results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get translation by ID", description = "Retrieve a specific ICD-11 translation by its ID")
    public ResponseEntity<ApiResponse<ICD11TranslationResponse>> getById(@PathVariable int id) {
        ICD11TranslationResponse result = translationService.mapToResponse(
                translationService.getById(id));
        return ResponseEntity.ok(ApiResponse.success("Translation retrieved successfully", result));
    }
}