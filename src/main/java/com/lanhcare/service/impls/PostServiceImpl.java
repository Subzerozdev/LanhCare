package com.lanhcare.service.impls;

import com.lanhcare.dto.media.PostRequest;
import com.lanhcare.dto.media.PostResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.Post;
import com.lanhcare.entity.PostMedia;
import com.lanhcare.exception.exps.LanhCareException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.PostRepository;
import com.lanhcare.service.PostService;
import com.lanhcare.specification.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;

    @Override
    public Post createPost(PostRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new LanhCareException("Account not found"));

        Post post = Post.builder()
                .content(request.getContent())
                .account(account)
                .isDeleted(false)
                .heart(0)
                .build();

        // Post Media
        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            for (int i = 0; i < request.getMediaUrls().size(); i++) {
                PostMedia media = PostMedia.builder()
                        .url(request.getMediaUrls().get(i))
                        .mediaType("IMAGE/VIDEO")
                        .orderIndex(i)
                        .post(post)
                        .build();
                post.getMediaList().add(media);
            }
        }
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Integer id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional.ofNullable(request.getHeart()).ifPresent(post::setHeart);
        Optional.ofNullable(request.getContent()).ifPresent(post::setContent);

        if (request.getMediaUrls() != null) {
            post.getMediaList().clear();
            for (int i = 0; i < request.getMediaUrls().size(); i++) {
                PostMedia media = PostMedia.builder()
                        .url(request.getMediaUrls().get(i))
                        .mediaType("IMAGE/VIDEO")
                        .orderIndex(i)
                        .post(post)
                        .build();
                post.getMediaList().add(media);
            }
        }
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    @Override
    public Page<PostResponse> getPostsByCriteria(String search, Pageable pageable) {
        Specification<Post> specification = PostSpec.filterByCriteria(search);
        Page<Post> postPage = postRepository.findAll(specification, pageable);
        return postPage.map(this::mapToPostResponse);
    }

    @Override
    @Transactional
    public List<PostResponse> getPostsByAccount(int accountId) {
        List<Post> posts = postRepository.getPostsByAccount_Id(accountId);
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .heart(post.getHeart())
                .createdAt(post.getCreatedAt())
                .accountId(post.getAccount().getId())
                .mediaUrls(post.getMediaList().stream().map(PostMedia::getUrl).toList())
                .build();
    }
}
