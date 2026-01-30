package com.lanhcare.dto.icd;

import com.lanhcare.enums.TranslationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICD11TranslationResponse {
    private Integer id;
    private String icdUri;
    private String vnTitle;
    private String vnDefinition;
    private TranslationStatus status;
}