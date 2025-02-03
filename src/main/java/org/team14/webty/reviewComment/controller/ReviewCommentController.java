package org.team14.webty.reviewComment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.team14.webty.common.dto.ApiResponse;
import org.team14.webty.user.dto.UserDto;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.service.ReviewCommentService;
import org.team14.webty.user.entity.WebtyUser;

import java.util.List;

@RestController
@RequestMapping("/api/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class ReviewCommentController {
    private final ReviewCommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
        @AuthenticationPrincipal WebtyUser user,
        @PathVariable Long reviewId,
        @RequestBody @Valid CommentRequest request
    ) {
        CommentResponse comment = commentService.createComment(user, reviewId, request);
        return ResponseEntity.ok(new ApiResponse<>(new UserDto(user), comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
        @AuthenticationPrincipal WebtyUser user,
        @PathVariable Long commentId,
        @RequestBody @Valid CommentRequest request
    ) {
        CommentResponse comment = commentService.updateComment(commentId, user, request);
        return ResponseEntity.ok(new ApiResponse<>(new UserDto(user), comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @AuthenticationPrincipal WebtyUser user,
        @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok(new ApiResponse<>(new UserDto(user), null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
        @AuthenticationPrincipal WebtyUser user,
        @PathVariable Long reviewId
    ) {
        List<CommentResponse> comments = commentService.getCommentsByReviewId(reviewId);
        return ResponseEntity.ok(new ApiResponse<>(new UserDto(user), comments));
    }
}
