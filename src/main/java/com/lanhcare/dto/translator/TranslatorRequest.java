package com.lanhcare.dto.translator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TranslatorRequest {
    @JsonProperty(value = "text")
    private String text;

    @JsonProperty(value = "target_language")
    private String targetLanguage;
}
