package com.lanhcare.service;

import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostService {
    Post createPost(PostRequest request);

    Post updatePost(Integer id, PostRequest request);

    PostResponse mapToPostResponse(Post post);

    void deletePost(Integer id);

    Page<PostResponse> getPostsByCriteria(String search, Pageable pageable);
}
