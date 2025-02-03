package org.team14.webty.review.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.review.dto.FeedReviewPageResponse;
import org.team14.webty.review.dto.FeedReviewResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final WebtoonRepository webtoonRepository;

	// id로 조회하기
	public FeedReviewResponse getReview(Long id) {

		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 reviewId"));

		review.plusViewCount(); // 조회수 증가

		reviewRepository.save(review);
		return convertToFeedReviewResponse(review);

	}

	//전체 리뷰 조회
	public List<FeedReviewResponse> getAllReviews() {
		return reviewRepository.findAll().stream()
			.map(this::convertToFeedReviewResponse)
			.collect(Collectors.toList());
	}

	//리뷰 생성
	public FeedReviewResponse createReview(ReviewRequest reviewRequest) {
		Webtoon webtoon = webtoonRepository.findById(reviewRequest.getWebtoon().getWebtoonId())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 webtoonId"));

		Review review = Review.builder()
			.content(reviewRequest.getContent())
			.title(reviewRequest.getTitle())
			.isSpoiler(reviewRequest.getIsSpoiler())
			.webtoon(webtoon)
			.build();

		Review savedReview = reviewRepository.save(review);

		return convertToFeedReviewResponse(savedReview);
	}

	//리뷰 삭제
	public void deleteReview(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 reviewId")); // or custom exception
		reviewRepository.delete(review);
	}

	//리뷰 수정
	public FeedReviewResponse updateReview(Long id, ReviewRequest reviewRequest) {
		Optional<Review> existingReview = reviewRepository.findById(id);
		if (existingReview.isPresent()) {
			Review updatedReview = existingReview.get();
			updatedReview.setContent(reviewRequest.getContent());
			updatedReview.setTitle(reviewRequest.getTitle());
			updatedReview.setSpoilerStatus(reviewRequest.getIsSpoiler());

			Webtoon webtoon = webtoonRepository.findById(reviewRequest.getWebtoon().getWebtoonId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 webtoonId"));
			updatedReview.setWebtoon(webtoon);

			Review savedReview = reviewRepository.save(updatedReview);
			return convertToFeedReviewResponse(savedReview);

		}
		return null;
	}

	// 특정 사용자의 리뷰 목록 조회
	public List<FeedReviewResponse> getReviewsByUser(Long userId) {
		return reviewRepository.findByUser_UserId(userId).stream()
			.map(this::convertToFeedReviewResponse)
			.collect(Collectors.toList());
	}

	// 조회수 내림차순으로 모든 리뷰 조회
	public List<FeedReviewResponse> getAllReviewsOrderByViewCountDesc() {
		return reviewRepository.findAllOrderByViewCountDesc().stream()
			.map(this::convertToFeedReviewResponse)
			.collect(Collectors.toList());
	}

	// 특정 사용자의 리뷰 개수 조회
	public Long getReviewCountByUser(Long userId) {
		return reviewRepository.countByUser_UserId(userId);
	}

	// 특정 사용자의 리뷰 목록 조회 (페이징 처리)
	public FeedReviewPageResponse getReviewsByUser(Long userId, Pageable pageable) {
		Page<Review> reviews = reviewRepository.findByUser_UserId(userId, pageable); // Pageable 객체로 페이징 처리
		Page<FeedReviewResponse> feedReviewResponses = reviews.map(
			this::convertToFeedReviewResponse); // Review 엔티티를 FeedReviewResponse DTO로 변환
		return FeedReviewPageResponse.from(feedReviewResponses); // Page<FeedReviewResponse>를 FeedReviewPageResponse로 변환
	}

	private FeedReviewResponse convertToFeedReviewResponse(Review review) {
		return FeedReviewResponse.builder()
			.reviewId(review.getReviewId())
			.user(review.getUser())
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.isSpoiler(review.isSpoiler)
			.webtoonId(review.getWebtoon().getWebtoonId().toString())
			.build();
	}

	public FeedReviewPageResponse getFeedReviews(Pageable pageable) {
		Page<Review> reviews = reviewRepository.findAll(pageable);
		Page<FeedReviewResponse> feedReviewResponses = reviews.map(this::convertToFeedReviewResponse);
		return FeedReviewPageResponse.from(feedReviewResponses);
	}

	public List<FeedReviewResponse> getAllFeedReviews() {
		return reviewRepository.findAll().stream()
			.map(this::convertToFeedReviewResponse)
			.collect(Collectors.toList());
	}
}
