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
public class ReleaseDTO {
    @JsonProperty("@context")
    private String context;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("title")
    private IcdTextDTO title;

    @JsonProperty("definition")
    private IcdTextDTO definition;

    @JsonProperty("child")
    private List<String> child;

    @JsonProperty("releaseDate")
    private String releaseDate;

    @JsonProperty("releaseId")
    private String releaseId;

    @JsonProperty("browserUrl")
    private String browserUrl;
}
