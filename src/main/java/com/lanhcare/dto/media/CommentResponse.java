package com.lanhcare.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Integer id;
    private String content;
    private LocalDateTime createdAt;
    private List<String> mediaUrls;
    private Integer accountId;
    private List<CommentResponse> replies;
}
