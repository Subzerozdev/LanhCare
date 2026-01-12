package com.lanhcare.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    @Bean
    @Qualifier("ragVectorStore")
    public VectorStore ragVectorStore(
            @Value("${spring.ai.vectorstore.pgvector.rag.table-name}") String tableName
    ) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .dimensions(384)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(true)
                .removeExistingVectorStoreTable(false)
                .build();
    }

    @Bean
    @Qualifier("chatMemoryVectorStore")
    public VectorStore chatMemoryVectorStore(
            @Value("${spring.ai.vectorstore.pgvector.chatmemory.table-name}") String tableName
    ) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .dimensions(384)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(true)
                .removeExistingVectorStoreTable(false)
                .build();
    }
}
