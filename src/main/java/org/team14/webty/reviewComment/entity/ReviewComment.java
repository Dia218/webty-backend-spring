package org.team14.webty.reviewComment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.team14.webty.review.entity.Review;
import org.team14.webty.user.entity.WebtyUser;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_comment")
public class ReviewComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private WebtyUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    // Adjacency List 방식으로 변경
    @Column(name = "parent_id")
    private Long parentId;  // 부모 댓글의 ID를 직접 저장

    @Column(name = "depth")
    private Integer depth;  // 댓글의 깊이 (0: 루트 댓글, 1: 대댓글, 2: 대대댓글...)

    @ManyToMany
    @JoinTable(
        name = "comment_mentions",
        joinColumns = @JoinColumn(name = "comment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<WebtyUser> mentionedUsers = new HashSet<>();

    @Builder
    public ReviewComment(WebtyUser user, Review review, String comment, Long parentId) {
        this.user = user;
        this.review = review;
        this.comment = comment;
        this.parentId = parentId;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        
        // depth 설정 로직 수정
        this.depth = (parentId == null) ? 0 : 1;  // 임시로 단순화
    }

    public void updateComment(String comment) {
        this.comment = comment;
        this.modifiedAt = LocalDateTime.now();
    }
}
