package org.team14.webty.review.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.review.dto.feedReviewResponse;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final WebtoonRepository webtoonRepository;

	// id로 조회하기
	public Review getReview(Long id) {

		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 reviewId"));

		review.plusViewCount(); // 조회수 증가

		reviewRepository.save(review);
		return review;

	}

	//전체 리뷰 조회
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	//리뷰 생성
	public Review createReivew(Review review) {
		return reviewRepository.save(review);
	}

	//리뷰 삭제
	public void deleteReview(Long id) {
		reviewRepository.deleteById(id);
	}

	//리뷰 수정
	public Review updateReview(Long id, Review review) {
		Optional<Review> existingReview = reviewRepository.findById(id);
		if (existingReview.isPresent()) {
			Review updatedReview = existingReview.get();
			updatedReview.setContent(review.getContent());
			return reviewRepository.save(updatedReview);
		}
		return null;
	}

	// 특정 사용자의 리뷰 목록 조회
	public List<Review> getReviewsByUser(Long userId) {
		return reviewRepository.findByUser_UserId(userId);
	}

	// 조회수 내림차순으로 모든 리뷰 조회
	public List<Review> getAllReviewsOrderByViewCountDesc() {
		return reviewRepository.findAllOrderByViewCountDesc();
	}

	// 특정 사용자의 리뷰 개수 조회
	public Long getReviewCountByUser(Long userId) {
		return reviewRepository.countByUser_UserId(userId);
	}

	// 특정 사용자의 리뷰 목록 조회 (페이징 처리)
	public List<Review> getReviewsByUser(Long userId, Pageable pageable) {
		return reviewRepository.findByUser_UserId(userId, pageable);
	}

	private feedReviewResponse convertToFeedReviewResponse(Review review) {
		return feedReviewResponse.builder()
			.reviewId(review.getReviewId())
			.user(review.getUser())
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.isSpoiler(review.isSpoiler())
			.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.webtoonId(review.getWebtoonId())
			.commentCount(0) // 임시 값 (추후 댓글 기능 구현 후 수정)
			.likeCount(0) // 임시 값 (추후 좋아요 기능 구현 후 수정)
			.build();
	}
}
