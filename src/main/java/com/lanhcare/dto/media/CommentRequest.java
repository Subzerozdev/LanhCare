package com.lanhcare.dto.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    private String content;
    private List<String> mediaUrls;
    private Integer postId;
    private Integer accountId;
    private Integer parentCommentId;
}