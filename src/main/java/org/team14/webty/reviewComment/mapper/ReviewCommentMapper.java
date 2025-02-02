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
        CommentResponse response = new CommentResponse();
        
        response.setUser(new UserDto(comment.getUser()));
        response.setCommentId(comment.getCommentId());
        response.setComment(comment.getComment());
        response.setCreatedAt(comment.getCreatedAt());
        response.setModifiedAt(comment.getModifiedAt());
        response.setDepth(comment.getDepth());
        response.setParentId(comment.getParentId());
        
        return response;
    }
} 