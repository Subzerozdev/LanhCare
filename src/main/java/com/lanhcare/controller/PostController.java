package com.lanhcare.controller;

import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestBody PostRequest request
    ) {
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
