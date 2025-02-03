package org.team14.webty.reviewComment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.common.dto.StandardResponse;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.service.ReviewCommentService;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.WebtyUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class ReviewCommentController {
	private final ReviewCommentService commentService;

	@PostMapping
	public ResponseEntity<StandardResponse<CommentResponse>> createComment(
		@AuthenticationPrincipal WebtyUser user,
		@PathVariable Long reviewId,
		@RequestBody @Valid CommentRequest request
	) {
		CommentResponse comment = commentService.createComment(user, reviewId, request);
		return ResponseEntity.ok(new StandardResponse<>(new UserDataResponse(user), comment));
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<StandardResponse<CommentResponse>> updateComment(
		@AuthenticationPrincipal WebtyUser user,
		@PathVariable Long commentId,
		@RequestBody @Valid CommentRequest request
	) {
		CommentResponse comment = commentService.updateComment(commentId, user, request);
		return ResponseEntity.ok(new StandardResponse<>(new UserDataResponse(user), comment));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<StandardResponse<Void>> deleteComment(
		@AuthenticationPrincipal WebtyUser user,
		@PathVariable Long commentId
	) {
		commentService.deleteComment(commentId, user);
		return ResponseEntity.ok(new StandardResponse<>(new UserDataResponse(user), null));
	}

	@GetMapping
	public ResponseEntity<StandardResponse<List<CommentResponse>>> getComments(
		@AuthenticationPrincipal WebtyUser user,
		@PathVariable Long reviewId
	) {
		List<CommentResponse> comments = commentService.getCommentsByReviewId(reviewId);
		return ResponseEntity.ok(new StandardResponse<>(new UserDataResponse(user), comments));
	}
}
