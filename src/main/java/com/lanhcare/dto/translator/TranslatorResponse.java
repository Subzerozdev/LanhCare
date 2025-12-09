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
public class TranslatorResponse {
    @JsonProperty(value = "success")
    private Boolean success;

    @JsonProperty(value = "translated_text")
    private String translatedText;

    @JsonProperty(value = "source_text")
    private String sourceText;

    @JsonProperty(value = "target_language")
    private String targetLanguage;

    @JsonProperty(value = "characters_used")
    private String charactersUsed;

    @JsonProperty(value = "characters_remaining")
    private String charactersRemaining;
}
