package com.lanhcare.controller;

import com.lanhcare.service.IcdApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/icd11")
@RequiredArgsConstructor
@Tag(name = "ICD11", description = "APIs for managing ICD11 Entity from WHO")
public class ICD11Controller {
    private final IcdApiService icdApiService;

    @PostMapping("/chapters")
    @Operation(summary = "Seed all chapter from WHO", description = "Seed all icd chapter from WHO")
    public ResponseEntity<String> seedChapters() {
        icdApiService.seedChaptersData();
        return ResponseEntity.ok("Seed all chapters from WHO successfully");
    }

    @PostMapping("/codes")
    @Operation(summary = "Seed sample codes from WHO", description = "Seed sample ICD codes from WHO")
    public ResponseEntity<String> seedSampleCodes() {
        icdApiService.seedSampleCodesData();
        return ResponseEntity.ok("Seed sample codes from WHO successfully");
    }
}
