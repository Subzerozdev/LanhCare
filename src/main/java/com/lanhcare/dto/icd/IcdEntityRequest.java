package com.lanhcare.dto.icd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IcdEntityRequest {
    private String code;
    private String chapterId;
}
