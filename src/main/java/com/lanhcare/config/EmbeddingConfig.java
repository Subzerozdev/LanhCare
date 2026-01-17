package com.lanhcare.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Embedding Configuration
 * - Production: Uses OpenAI Embeddings (lightweight, API-based)
 * - Development: Uses ONNX Transformers (local, heavy)
 */
@Configuration
public class EmbeddingConfig {

    /**
     * Production: Use OpenAI Embeddings
     * This is lightweight and doesn't cause OutOfMemoryError
     */
    @Bean
    @Primary
    @Profile("prod")
    public EmbeddingModel openAiEmbeddingModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.embedding.base-url:https://api.openai.com}") String baseUrl
    ) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
        
        return new OpenAiEmbeddingModel(openAiApi);
    }
}
