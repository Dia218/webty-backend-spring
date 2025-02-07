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
import org.team14.webty.recommend.service.RecommendService;
import org.team14.webty.security.authentication.WebtyUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {
	private final RecommendService recommendService;

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
}
