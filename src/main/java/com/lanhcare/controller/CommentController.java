package com.lanhcare.controller;

import com.lanhcare.dto.media.CommentRequest;
import com.lanhcare.dto.media.CommentResponse;
import com.lanhcare.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.mapToCommentResponse(
                commentService.createComment(request)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Integer id,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.mapToCommentResponse(
                commentService.updateComment(id, request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getTopLevelComments(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getTopLevelComments(postId));
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Integer parentId) {
        return ResponseEntity.ok(commentService.getRepliesByParentId(parentId));
    }
}
