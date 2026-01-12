package com.lanhcare.service;

import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.entity.Post;

public interface PostService {
    Post createPost(PostRequest request);

    Post updatePost(Integer id, PostRequest request);

    PostResponse mapToPostResponse(Post post);

    void deletePost(Integer id);
}
