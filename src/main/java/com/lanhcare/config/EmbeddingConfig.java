package com.lanhcare.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Embedding Configuration
 * - Production: Uses OpenAI/Gemini Embeddings (lightweight, API-based)
 * - Development: Uses ONNX Transformers (local, heavy)
 */
@Configuration
public class EmbeddingConfig {

    /**
     * Production: Use OpenAI-compatible Embeddings (works with Gemini API)
     * This is lightweight and doesn't cause OutOfMemoryError
     */
    @Bean("productionEmbeddingModel")
    @ConditionalOnMissingBean(EmbeddingModel.class)
//    @Profile("prod")
    @ConditionalOnProperty(name = "spring.ai.transformers.enabled", havingValue = "false", matchIfMissing = true)
    public OpenAiEmbeddingModel productionEmbeddingModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.embedding.base-url:https://generativelanguage.googleapis.com}") String baseUrl
    ) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
        
        return new OpenAiEmbeddingModel(openAiApi);
    }
}
