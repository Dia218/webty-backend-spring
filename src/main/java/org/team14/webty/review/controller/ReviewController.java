package org.team14.webty.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.review.dto.FeedReviewDetailResponse;
import org.team14.webty.review.dto.FeedReviewResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.service.ReviewService;
import org.team14.webty.security.authentication.WebtyUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	// id로 조회하기
	@GetMapping("/{reviewId}")
	public ResponseEntity<FeedReviewDetailResponse> getReview(@PathVariable(value = "reviewId") Long reviewId,
		@RequestParam(defaultValue = "0", value = "page") int page,
		@RequestParam(defaultValue = "10", value = "size") int size) {
		return ResponseEntity.ok(reviewService.getFeedReview(reviewId, page, size));
	}

	//전체 리뷰 조회
	@GetMapping
	public ResponseEntity<Page<FeedReviewResponse>> getAllFeedReviews(
		@RequestParam(defaultValue = "0", value = "page") int page,
		@RequestParam(defaultValue = "10", value = "size") int size) {
		return ResponseEntity.ok(reviewService.getAllFeedReviews(page, size));
	}

	//리뷰 생성
	@PostMapping("/create")
	public ResponseEntity<Long> createFeedReview(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@RequestBody ReviewRequest reviewRequest) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(reviewService.createFeedReview(webtyUserDetails, reviewRequest));
	}

	//리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteFeedReview(@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@PathVariable(value = "reviewId") Long reviewId) {
		reviewService.deleteFeedReview(webtyUserDetails, reviewId);
		return ResponseEntity.ok().build();
	}

	//리뷰 수정
	@PutMapping("/{reviewId}")
	public ResponseEntity<Long> updateFeedReview(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails, @PathVariable(value = "reviewId") Long reviewId,
		@RequestBody ReviewRequest reviewRequest) {
		return ResponseEntity.ok().body(reviewService.updateFeedReview(webtyUserDetails, reviewId, reviewRequest));
	}

	// 특정 사용자의 리뷰 목록 조회
	@GetMapping("/me")
	public ResponseEntity<List<FeedReviewResponse>> getReviewsByUser(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails) {
		return ResponseEntity.ok().body(reviewService.getReviewsByUser(webtyUserDetails));
	}

	// 조회수 내림차순으로 모든 리뷰 조회
	@GetMapping("/view-count-desc")
	public ResponseEntity<Page<FeedReviewResponse>> getAllReviewsOrderByViewCountDesc(
		@RequestParam(defaultValue = "0", value = "page") int page,
		@RequestParam(defaultValue = "10", value = "size") int size) {
		return ResponseEntity.ok(reviewService.getAllReviewsOrderByViewCountDesc(page, size));
	}

	// 특정 사용자의 리뷰 개수 조회
	@GetMapping("/me/count")
	public ResponseEntity<Long> getReviewCountByUser(@AuthenticationPrincipal WebtyUserDetails webtyUserDetails) {
		return ResponseEntity.ok(reviewService.getReviewCountByUser(webtyUserDetails));
	}

}
