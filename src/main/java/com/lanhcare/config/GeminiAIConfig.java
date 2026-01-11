package com.lanhcare.config;

import com.lanhcare.exception.AIException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
@RequiredArgsConstructor
public class GeminiAIConfig {
    @Bean
    public MessageWindowChatMemory messageWindowChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(5)
                .build();
    }

    @Bean
    @Primary
    public EmbeddingModel defaultEmbeddingModel(EmbeddingModel embeddingModel) {
        return embeddingModel;
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClient,
            @Qualifier("ragVectorStore") VectorStore ragVectorStore
    ) {
        // 1. Instructions
        String systemInstructions = readSystemInstructions();

        return chatClient
                .defaultSystem(systemInstructions)
                .defaultAdvisors(new QuestionAnswerAdvisor(ragVectorStore))
                .build();
    }

    public String readSystemInstructions() {
        ClassPathResource resource = new ClassPathResource("prompts/systemInstructions.txt");
        StringBuilder content = new StringBuilder();

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new AIException("Failed to read system instructions.txt: " + e.getMessage());
        }
        return content.toString();
    }
}
