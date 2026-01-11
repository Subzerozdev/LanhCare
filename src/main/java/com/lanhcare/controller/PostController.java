package com.lanhcare.controller;

import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AccountService;
import com.lanhcare.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "User - Post Feature", description = "APIs for user to post their story or media")
public class PostController {
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestBody PostRequest request,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();
        request.setAccountId(accountId);

        return ResponseEntity.ok(postService.mapToPostResponse(
                postService.createPost(request)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Integer id, @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.mapToPostResponse(
                postService.updatePost(id, request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
