package com.lanhcare.controller;

import com.lanhcare.dto.icd.IcdEntityDTO;
import com.lanhcare.dto.icd.ReleaseDTO;
import com.lanhcare.entity.ICD11Chapter;
import com.lanhcare.service.IcdApiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final IcdApiService icdApiService;

    @GetMapping("/icd/access-token")
    @Operation(summary = "Get icd access token", description = "Get icd access token")
    public ResponseEntity<String> getAccessToken() {
        String accessToken = icdApiService.getAccessToken().getAccessToken();
        return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
    }

    @GetMapping("/icd/chapter")
    @Operation(summary = "Get All Chapters", description = "Get All Chapters")
    public ResponseEntity<?> getChapters() {
        ReleaseDTO chapters = icdApiService.fetchReleaseChapters();
        return ResponseEntity.status(HttpStatus.CREATED).body(chapters);
    }

    @GetMapping("/icd/chapter-code")
    @Operation(summary = "Get All Chapter Codes", description = "Get All Chapter Codes")
    public ResponseEntity<?> getChapterCodes() {
        ReleaseDTO chapters = icdApiService.fetchReleaseChapters();
        return ResponseEntity.status(HttpStatus.CREATED).body(icdApiService.getChapterCodes(chapters));
    }

    @GetMapping("/icd/entity/{id}")
    @Operation(summary = "Get ICD Entity", description = "Get ICD Entity")
    public ResponseEntity<?> getEntityById(
            @PathVariable String id
    ) {
        IcdEntityDTO entity = icdApiService.fetchIcdEntity(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @GetMapping("/icd/all-chapter")
    @Operation(summary = "Get All Chapter Entity", description = "Get All Chapter Entity")
    public ResponseEntity<?> getChapterEntities() {
        List<ICD11Chapter> chapters = icdApiService.seedICDData();
        return ResponseEntity.status(HttpStatus.CREATED).body(chapters);
    }
}
