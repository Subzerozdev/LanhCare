package com.lanhcare.dto.admin.comment;

import com.lanhcare.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Comment in Admin panel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommentResponse {
    private Integer id;
    private String content;
    private Boolean isDeleted;
    private CommentStatus status;
    private String rejectionReason;
    private String createdAt;
    private Integer postId;
    private Integer authorId;
    private String authorName;
    private String authorEmail;
    private Integer parentCommentId;
    private Long replyCount;
    private Integer mediaCount;
    private List<String> mediaUrls;
}
