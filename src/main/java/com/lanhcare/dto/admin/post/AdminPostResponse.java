package com.lanhcare.dto.admin.post;

import com.lanhcare.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Post in Admin panel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostResponse {
    private Integer id;
    private String content;
    private Integer heart;
    private Boolean isDeleted;
    private PostStatus status;
    private String rejectionReason;
    private String createdAt;
    private Integer authorId;
    private String authorName;
    private String authorEmail;
    private Long commentCount;
    private Integer mediaCount;
    private List<String> mediaUrls;
}
