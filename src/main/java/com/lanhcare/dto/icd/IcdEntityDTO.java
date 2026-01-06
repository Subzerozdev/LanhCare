package com.lanhcare.dto.icd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IcdEntityDTO {
    @JsonProperty(value = "@id")
    private String icdUri;

    @JsonProperty(value = "code")
    private String icdCode;

    @JsonProperty(value = "title")
    private IcdTextDTO originalTitleEn;

    @JsonProperty(value = "definition")
    private IcdTextDTO definitionEn;

    @JsonProperty(value = "classKind")
    private String type;

    @JsonProperty(value = "parent")
    private List<String> parentUri;

    @JsonProperty(value = "child")
    private List<String> children;
}
