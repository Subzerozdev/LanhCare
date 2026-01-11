package com.lanhcare.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIRequest {
    private String message;
//    private String conversationId;
    private Boolean isSpeech;
//    private String token;
//    private Boolean isMember;
}
