package com.lanhcare.service;

import com.lanhcare.dto.media.CommentRequest;
import com.lanhcare.dto.media.CommentResponse;
import com.lanhcare.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(CommentRequest request);

    Comment updateComment(Integer id, CommentRequest request);

    CommentResponse mapToCommentResponse(Comment comment);

    void deleteComment(Integer id);

    List<CommentResponse> getTopLevelComments(Integer postId);

    List<CommentResponse> getRepliesByParentId(Integer parentId);
}
