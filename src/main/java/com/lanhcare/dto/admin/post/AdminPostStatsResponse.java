package com.lanhcare.dto.admin.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Statistics Response DTO for Posts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostStatsResponse {
    private Long totalPosts;
    private Long activePosts;
    private Long deletedPosts;
    private Long pendingPosts;
    private Long approvedPosts;
    private Long rejectedPosts;
    private Long totalComments;
    private Long activeComments;
}
