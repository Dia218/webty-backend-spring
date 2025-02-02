package org.team14.webty.review.service;

import org.springframework.stereotype.Service;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public Review getReview(Long id) {

		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 reviewId"));

		review.plusViewCount(); // 조회수 증가

		reviewRepository.save(review);
		return review;

	}
}
