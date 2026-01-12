package com.lanhcare.controller;

import com.lanhcare.dto.ai.AIRequest;
import com.lanhcare.dto.ai.AIResponse;
import com.lanhcare.service.AIService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/ai")
@Tag(name = "User - AI Chatbot", description = "APIs for user AI Chatbot")
public class AIController {
    private final AIService aiService;

    @PostMapping("/prompt")
    public ResponseEntity<?> promptAI(
            @RequestBody AIRequest request,
            @RequestHeader(value = "Authorization", required = false) String token
    ) throws IOException {
//        request.setIsMember(token != null);
//        request.setToken(token);
        AIResponse response = aiService.generateResponse(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
