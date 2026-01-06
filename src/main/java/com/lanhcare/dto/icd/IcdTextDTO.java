package com.lanhcare.dto.icd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IcdTextDTO {
    @JsonProperty("@language")
    private String language;

    @JsonProperty("@value")
    private String value;
}
