package com.lanhcare.service.impls;

import com.lanhcare.dto.ai.AIRequest;
import com.lanhcare.dto.ai.AIResponse;
import com.lanhcare.exception.exps.AIException;
import com.lanhcare.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("ragVectorStore")
    private VectorStore ragVectorStore;

    @Autowired
    @Qualifier("chatMemoryVectorStore")
    private VectorStore chatMemoryVectorStore;

    @Value("${app.tts-api.url}")
    private String ttsUrl;

    @Override
    public AIResponse generateResponse(AIRequest request)  throws IOException {
        log.info("Prepare to request");

        String message = request.getMessage();
        String conversationId = conversationId(request);

        VectorStoreChatMemoryAdvisor chatMemoryAdvisor = VectorStoreChatMemoryAdvisor
                .builder(chatMemoryVectorStore)
                .conversationId(conversationId)
                .defaultTopK(10)
                .build();

        log.info("Prepare Advisor");

        // 1. Perform a similarity search in the vector store
        // 'R' (Retrieval) in RAG
        List<Document> similarDocuments = ragVectorStore.similaritySearch(
                SearchRequest.builder().query(message)
                        // Retrieve top 10 most similar documents
                        .topK(10)
                        .build()
        );

        log.info("Prepare Doc");

        // 2. Extract relevant content from retrieved documents
        assert similarDocuments != null;
        String context = similarDocuments.stream()
                .map(Document::getText)
                // Combine them into a single context string
                .collect(Collectors.joining("\n\n"));

        String metadataInfo = similarDocuments.stream()
                .map(this::extractFromDocument)
                .collect(Collectors.joining("\n"));

        String orderInfo = "none";
        if (request.getCreateOrderRequest() != null) {
            orderInfo = request.getCreateOrderRequest().toString();
        }

        log.info("Prepare Prompt");

        // 3. Construct a prompt for the LLM using the retrieved context
        String promptText = """                

                User Question: {message}
                
                """;

        log.info("Prepare Template");

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(
                Map.of(
                        "context", context,
                        "metadataInfo", metadataInfo,
                        "message", message,
                        "orderInfo", orderInfo,
                        "userAccount", Boolean.TRUE.equals(request.getIsMember()) ? "member" : "none"
                )
        );

        log.info("Ready to send request");

        // 4. Send enriched prompt to the LLM
        // 'G' (Generation) in RAG
        AIResponse aiResponse = chatClient
                .prompt(prompt)
                .advisors(chatMemoryAdvisor)
                .call()
                .entity(AIResponse.class);

        log.info("Response: {}", aiResponse);

        assert aiResponse != null;
        if (aiResponse.getMessageType().equals("order")
                && Boolean.TRUE.equals(aiResponse.getIsAcceptBooking())
                && validateOrderFields(aiResponse.getCreateOrderRequest())
        ) {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) bookingScheduleSeat(request, aiResponse);
            aiResponse.setMessageRoute((String) response.get("paymentUrl"));
            aiResponse.setMessageType("order");
        }
        aiResponse.setConversationId(conversationId);
        aiResponse.setAudioBase64(null);

        if (isFastApiHealthy() && request.getIsSpeech().equals(Boolean.TRUE)) {
            aiResponse.setAudioBase64(Base64.getEncoder().encodeToString(synthesizeSpeech(aiResponse.getMessage())));
        }

        log.info("Response after audio process: {}", aiResponse);

        return aiResponse;
    }

    @Override
    public byte[] synthesizeSpeech(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new ConcurrentHashMap<>();
        requestBody.put("text", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        try {
            String fastApiTtsUrl = ttsUrl + "/synthesize_speech";
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiTtsUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String base64Audio = (String) response.getBody().get("audio_base64");
                if (base64Audio != null && !base64Audio.isEmpty()) {
                    return Base64.getDecoder().decode(base64Audio);
                }
            }
            return new byte[0];
        } catch (AIException e) {
            return new byte[0];
        }
    }

    @Override
    public boolean isFastApiHealthy() {
        try {
            String fastApiHealthUrl = ttsUrl;
            ResponseEntity<Map> response = restTemplate.getForEntity(fastApiHealthUrl, Map.class);

            return response.getStatusCode().is2xxSuccessful()
                    && Boolean.TRUE.equals(Objects.requireNonNull(response.getBody()).get("model_loaded"));
        } catch (AIException e) {
            return false;
        }
    }

    private String extractFromDocument(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(':')
                    .append(entry.getValue())
                    .append(". ");
        }

        return stringBuilder.toString();
    }

    private String conversationId(AIRequest request) {
        // Generate or retrieve conversation ID
        if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
            return request.getConversationId();
        }
        // Generate new conversation ID (simple implementation)
        return "conv_" + System.currentTimeMillis();
    }

    private boolean validateOrderFields(Object createOrderRequest) {
        // TODO: Implement order validation logic
        if (createOrderRequest == null) {
            return false;
        }
        // Add validation logic here
        return true;
    }

    private Map<String, Object> bookingScheduleSeat(AIRequest request, AIResponse aiResponse) {
        // TODO: Implement booking logic
        // This is a placeholder - implement actual booking service call
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("paymentUrl", "https://payment.example.com/checkout");
        return result;
    }
}
