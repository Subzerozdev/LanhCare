package com.lanhcare.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {
    private final JdbcTemplate jdbcTemplate;

//    @Lazy
    @Bean
    @Qualifier("ragVectorStore")
    public VectorStore ragVectorStore(
            EmbeddingModel embeddingModel,
            @Value("${spring.ai.vectorstore.pgvector.rag.table-name}") String tableName,
            @Value("${spring.ai.vectorstore.pgvector.dimensions:768}") int dimensions
    ) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .dimensions(dimensions)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(true)
                .removeExistingVectorStoreTable(false)
                .build();
    }

//    @Lazy
    @Bean
    @Qualifier("chatMemoryVectorStore")
    public VectorStore chatMemoryVectorStore(
            EmbeddingModel embeddingModel,
            @Value("${spring.ai.vectorstore.pgvector.chatmemory.table-name}") String tableName,
            @Value("${spring.ai.vectorstore.pgvector.dimensions:768}") int dimensions
    ) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(tableName)
                .dimensions(dimensions)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(true)
                .removeExistingVectorStoreTable(false)
                .build();
    }
}
