package org.team14.webty.reviewComment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.team14.webty.reviewComment.entity.ReviewComment;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    // 특정 리뷰의 모든 댓글을 depth와 생성일시 순으로 조회
    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.reviewId = :reviewId " +
        "ORDER BY rc.depth ASC, rc.commentId DESC")
    Page<ReviewComment> findAllByReviewIdOrderByDepthAndCommentId(
        @Param("reviewId") Long reviewId,
        Pageable pageable
    );

    // 특정 댓글의 대댓글 목록 조회(댓글 삭제 시 하위 댓글들 조회 목적)
    List<ReviewComment> findByParentIdOrderByCommentIdAsc(Long parentId);

    // 부모 댓글 ID 기준으로 정렬된 댓글 목록 조회
    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.reviewId = :reviewId " +
        "ORDER BY CASE WHEN rc.depth = 0 THEN rc.commentId " +
        "ELSE (SELECT p.commentId FROM ReviewComment p WHERE p.commentId = rc.parentId) END DESC, " +
        "rc.depth ASC, rc.commentId DESC")
    List<ReviewComment> findAllByReviewIdOrderByParentCommentIdAndDepth(@Param("reviewId") Long reviewId);

    // 여러 리뷰의 댓글을 한 번에 조회 (N+1 문제 방지)
    @Query("SELECT rc FROM ReviewComment rc WHERE rc.review.reviewId IN :reviewIds ORDER BY rc.commentId DESC")
    List<ReviewComment> findAllByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}

