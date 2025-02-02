package org.team14.webty.reviewComment.mapper;

import org.springframework.stereotype.Component;
import org.team14.webty.review.entity.Review;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.dto.UserDto;

@Component
public class ReviewCommentMapper {
    
    public ReviewComment toEntity(CommentRequest request, WebtyUser user, Review review) {
        return ReviewComment.builder()
                .user(user)
                .review(review)
                .comment(request.getComment())
                .parentId(request.getParentCommentId())
                .build();
    }
    
    public CommentResponse toResponse(ReviewComment comment) {
        return CommentResponse.builder()
                .user(new UserDto(comment.getUser()))
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .depth(comment.getDepth())
                .parentId(comment.getParentId())
                .build();
    }
} 