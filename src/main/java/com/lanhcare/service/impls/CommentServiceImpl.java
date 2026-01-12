package com.lanhcare.service.impls;

import com.lanhcare.dto.media.CommentRequest;
import com.lanhcare.dto.media.CommentResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.Comment;
import com.lanhcare.entity.CommentMedia;
import com.lanhcare.entity.Post;
import com.lanhcare.exception.exps.LanhCareException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.CommentRepository;
import com.lanhcare.repository.PostRepository;
import com.lanhcare.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;


    @Override
    public Comment createComment(CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new LanhCareException("Post not found"));
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new LanhCareException("Account not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .account(account)
                .isDeleted(false)
                .build();

        // Check if comment is child of another comment
        if (request.getParentCommentId() != null && request.getParentCommentId() != 0) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new LanhCareException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        // Comment Media
        if (request.getMediaUrls() != null) {
            request.getMediaUrls().forEach(url -> {
                CommentMedia media = CommentMedia.builder()
                        .url(url)
                        .mediaType("IMAGE/VIDEO")
                        .comment(comment)
                        .build();
                comment.getMediaList().add(media);
            });
        }

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Integer id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(request.getContent());

        if (request.getMediaUrls() != null) {
            comment.getMediaList().clear();
            request.getMediaUrls().forEach(url -> {
                CommentMedia media = CommentMedia.builder().url(url).comment(comment).build();
                comment.getMediaList().add(media);
            });
        }
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    public CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .accountId(comment.getAccount().getId())
                .mediaUrls(comment.getMediaList().stream().map(CommentMedia::getUrl).toList())
                .replies(comment.getReplies().stream()
                        .filter(r -> !r.getIsDeleted())
                        .map(this::mapToCommentResponse).toList())
                .build();
    }

    @Override
    public List<CommentResponse> getTopLevelComments(Integer postId) {
        List<Comment> comments = commentRepository
                .findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId);

        return comments.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getRepliesByParentId(Integer parentId) {
        List<Comment> replies = commentRepository
                .findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentId);

        return replies.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }
}
