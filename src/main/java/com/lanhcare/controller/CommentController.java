package com.lanhcare.controller;

import com.lanhcare.dto.media.CommentRequest;
import com.lanhcare.dto.media.CommentResponse;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AccountService;
import com.lanhcare.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "User - Comment Post", description = "APIs for user to comment the post")
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @RequestBody CommentRequest request,
            @RequestHeader("Authorization") String token
    ) {
        int accountId = Integer.parseInt( jwtTokenProvider.getIdentifierFromToken(token));
        request.setAccountId(accountId);
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
