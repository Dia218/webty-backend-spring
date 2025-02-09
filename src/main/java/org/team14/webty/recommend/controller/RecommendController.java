package org.team14.webty.recommend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.common.dto.PageDto;
import org.team14.webty.common.mapper.PageMapper;
import org.team14.webty.recommend.service.RecommendService;
import org.team14.webty.review.dto.ReviewItemResponse;
import org.team14.webty.review.service.ReviewService;
import org.team14.webty.security.authentication.WebtyUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {
	private final RecommendService recommendService;
	private final ReviewService reviewService;

	@PostMapping("/{reviewId}")
	public ResponseEntity<Long> createRecommend(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@PathVariable(value = "reviewId") Long reviewId,
		@RequestParam(value = "type") String type
	){
		return ResponseEntity.ok(recommendService.createRecommend(webtyUserDetails,reviewId,type));
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteRecommend(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@PathVariable(value = "reviewId") Long reviewId,
		@RequestParam(value = "type") String type
	){
		recommendService.deleteRecommend(webtyUserDetails,reviewId,type);
		return ResponseEntity.ok().build();
	}

	// 단독 호출 할 일 없을듯
	@GetMapping("/{reviewId}")
	public ResponseEntity<Map<String,Long>> getRecommendCounts(
		@PathVariable(value = "reviewId") Long reviewId
	){
		Map<String, Long> recommendCounts = recommendService.getRecommendCounts(reviewId);
		return ResponseEntity.ok(recommendCounts);
	}

	// 특정 유저의 추천 리뷰 조회
	@GetMapping("/user/{userId}")
	public ResponseEntity<PageDto<ReviewItemResponse>> getUserRecommendReviews(
		@PathVariable(value = "userId") Long userId,
		@RequestParam(defaultValue = "0", value = "page") int page,
		@RequestParam(defaultValue = "10", value = "size") int size
	){
		return ResponseEntity.ok(PageMapper.toPageDto(reviewService.getUserRecommendedReviews(userId, page, size)));
	}
	
	// 특정 리뷰의 로그인된 유저 추천 현황 조회
	@GetMapping("/{reviewId}/recommendation")
	public ResponseEntity<Map<String, Boolean>> getRecommended(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@PathVariable(value="reviewId") Long reviewId
	){
		return ResponseEntity.ok(recommendService.isRecommended(webtyUserDetails,reviewId));
	}
}
