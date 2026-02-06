package com.lanhcare.controller;

import com.lanhcare.dto.admin.post.AdminPostDetailResponse;
import com.lanhcare.dto.admin.post.AdminPostResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.PostService;
import com.lanhcare.service.admin.AdminPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/public/posts")
@RequiredArgsConstructor
@Tag(name = "User - Post Feature", description = "APIs for user to post their story or media")
public class PostController {
    private final PostService postService;
    private final AdminPostService postAdminService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "Get all posts", description = "Get paginated list of posts with filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminPostResponse>>> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<AdminPostResponse> posts = postAdminService.getAllPosts(
                search, userId, isDeleted, startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post detail", description = "Get detailed information about a post")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> getPostDetail(@PathVariable Integer id) {
        AdminPostDetailResponse post = postAdminService.getPostDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
    }

//    @GetMapping
//    @Operation(summary = "Get all posts", description = "Get paginated list of posts with filters")
//    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllFoodItems(
//            @RequestParam(required = false) String search,
//            Pageable pageable)
//    {
//        Page<PostResponse> posts = postService.getPostsByCriteria(search, pageable);
//        return ResponseEntity.ok(ApiResponse.success("Food items retrieved successfully", posts));
//    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestBody PostRequest request,
            @RequestHeader("Authorization") String token
    ) {
        int accountId = Integer.parseInt( jwtTokenProvider.getIdentifierFromToken(token));
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
