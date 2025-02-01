package org.team14.webty.reviewComment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.user.entity.WebtyUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentService {
    private final ReviewCommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public CommentResponse createComment(WebtyUser user, Long reviewId, CommentRequest request) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        Long parentId = request.getParentCommentId();
        if (parentId != null) {
            // 부모 댓글이 존재하는지 확인
            ReviewComment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            if (parentComment.getDepth() >= 2) {
                throw new IllegalArgumentException("더 이상 대댓글을 작성할 수 없습니다.");
            }
        }

        ReviewComment comment = ReviewComment.builder()
            .user(user)
            .review(review)
            .comment(request.getComment())
            .parentId(parentId)
            .build();

        ReviewComment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, WebtyUser user, CommentRequest request) {
        ReviewComment comment = commentRepository.findById(commentId)
            .orElseThrow(CommentException::notFound);

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw CommentException.notAuthorized();
        }

        comment.updateComment(request.getComment());
        return new CommentResponse(comment);
    }

    @Transactional
    @CacheEvict(value = "comments", key = "#commentId")
    public void deleteComment(Long commentId, WebtyUser user) {
        ReviewComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }

        // 대댓글이 있는 경우의 처리 정책 명확화 필요
        List<ReviewComment> childComments = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
        if (!childComments.isEmpty()) {
            // 정책 1: 대댓글도 모두 삭제
            commentRepository.deleteAll(childComments);
            commentRepository.delete(comment);
            
            // 정책 2: 소프트 삭제 (내용만 변경)
            // comment.updateComment("삭제된 댓글입니다");
            // comment.markAsDeleted();  // 삭제 표시 필드 추가 필요
        } else {
            commentRepository.delete(comment);
        }
    }

    @Cacheable(value = "comments", key = "#reviewId")
    public List<CommentResponse> getCommentsByReviewId(Long reviewId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<ReviewComment> commentPage = commentRepository
            .findAllByReviewIdOrderByDepthAndCreatedAt(reviewId, pageable);
        List<ReviewComment> allComments = commentPage.getContent();  // Page를 List로 변환
        
        // 댓글들을 Map으로 구성 (parentId를 키로 사용)
        Map<Long, List<CommentResponse>> commentMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();
        
        // 댓글 트리 구조 구성
        for (ReviewComment comment : allComments) {
            CommentResponse response = new CommentResponse(comment);
            if (comment.getParentId() == null) {
                rootComments.add(response);
            } else {
                commentMap
                    .computeIfAbsent(comment.getParentId(), k -> new ArrayList<>())
                    .add(response);
            }
        }
        
        // 각 댓글의 대댓글 설정
        for (CommentResponse response : rootComments) {
            setChildComments(response, commentMap);
        }
        
        return rootComments;
    }

    private void setChildComments(CommentResponse parent, Map<Long, List<CommentResponse>> commentMap) {
        List<CommentResponse> children = commentMap.getOrDefault(parent.getCommentId(), new ArrayList<>());
        parent.setChildComments(children);
        for (CommentResponse child : children) {
            setChildComments(child, commentMap);
        }
    }

    // CommentException을 내부 static 클래스로 이동
    public static class CommentException extends RuntimeException {
        public CommentException(String message) {
            super(message);
        }
        
        public static CommentException notFound() {
            return new CommentException("댓글을 찾을 수 없습니다.");
        }
        
        public static CommentException notAuthorized() {
            return new CommentException("댓글 작성자만 수정/삭제할 수 있습니다.");
        }
    }
}
