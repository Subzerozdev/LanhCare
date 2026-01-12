package com.lanhcare.service;

import com.lanhcare.dto.ai.AIRequest;
import com.lanhcare.dto.ai.AIResponse;

import java.io.IOException;

public interface AIService {
    AIResponse generateResponse(AIRequest request) throws IOException;
}
