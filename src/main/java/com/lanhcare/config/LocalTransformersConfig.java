package com.lanhcare.config;

import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Local ONNX Transformers Configuration
 * Only enabled in development (non-prod profile)
 * 
 * This loads heavy ONNX models into memory which requires 1GB+ RAM.
 * Disabled on production to avoid OutOfMemoryError on Render Free tier.
 */
@Configuration
@Profile("!prod")
public class LocalTransformersConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.ai.transformers.enabled", havingValue = "true", matchIfMissing = true)
    public TransformersEmbeddingModel transformersEmbeddingModel() {
        return new TransformersEmbeddingModel();
    }
}
