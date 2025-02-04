package org.team14.webty.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.team14.webty.review.dto.FeedReviewResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.dto.SearchReviewResponse;
import org.team14.webty.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	// id로 조회하기
	@GetMapping("/{reviewId}")
	public ResponseEntity<FeedReviewResponse> getReview(@PathVariable Long reviewId) {
		return ResponseEntity.ok(reviewService.getFeedReview(reviewId));
	}

	//전체 리뷰 조회
	@GetMapping
	public ResponseEntity<List<FeedReviewResponse>> getAllFeedReviews() {
		return ResponseEntity.ok(reviewService.getAllFeedReviews());
	}

	//리뷰 생성
	@PostMapping
	public ResponseEntity<FeedReviewResponse> createFeedReview(@RequestBody ReviewRequest reviewRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createFeedReview(reviewRequest));
	}

	//리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteFeedReview(@PathVariable Long reviewId) {
		reviewService.deleteFeedReview(reviewId);
		return ResponseEntity.noContent().build();
	}

	//리뷰 수정
	@PutMapping("/{reviewId}")
	public ResponseEntity<FeedReviewResponse> updateFeedReview(@PathVariable Long reviewId,
		@RequestBody ReviewRequest reviewRequest) {
		FeedReviewResponse updatedReview = reviewService.updateFeedReview(reviewId, reviewRequest);
		return updatedReview != null ?
			ResponseEntity.ok(updatedReview) :
			ResponseEntity.notFound().build();
	}

	// 특정 사용자의 리뷰 목록 조회
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<SearchReviewResponse>> getReviewsByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
	}

	// 조회수 내림차순으로 모든 리뷰 조회
	@GetMapping("/all/view_count")
	public ResponseEntity<List<SearchReviewResponse>> getAllReviewsOrderByViewCountDesc() {
		return ResponseEntity.ok(reviewService.getAllReviewsOrderByViewCountDesc());
	}

	// 특정 사용자의 리뷰 개수 조회
	@GetMapping("/user/{userId}/count")
	public ResponseEntity<Long> getReviewCountByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(reviewService.getReviewCountByUser(userId));
	}

}
