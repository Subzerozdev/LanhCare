package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CommentMedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String url;

    @Column(name = "media_type", length = 20)
    private String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}