package com.lanhcare.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {
//    private String conversationId;
//    private String messageType;
//    private String messageRoute;
    private String message;
    private String audioBase64;
}
