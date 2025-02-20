package org.team14.webty.reviewComment.mapper;

import org.springframework.stereotype.Component;
import org.team14.webty.review.entity.Review;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.WebtyUser;

@Component
public class ReviewCommentMapper {

    public static ReviewComment toEntity(CommentRequest request, WebtyUser user, Review review) {
        return ReviewComment.builder()
            .user(user)
            .review(review)
            .content(request.getContent())
            .parentId(request.getParentCommentId())
            .mentions(request.getMentions())
            .depth(request.getParentCommentId() != null ? 1 : 0)  // 부모 댓글 여부에 따라 depth 설정
            .build();
    }

    public static CommentResponse toResponse(ReviewComment comment) {
        return CommentResponse.builder()
            .user(new UserDataResponse(comment.getUser()))
            .commentId(comment.getCommentId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .parentId(comment.getParentId())
            .mentions(comment.getMentions())
            .build();
    }
} 
