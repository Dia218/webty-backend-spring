package org.team14.webty.review.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public Review getReview(Long id) {
		Optional<Review> review = reviewRepository.findById(id);

		if (review.isPresent()) {
			Review review1 = Review.get();
			review.setViewCount(review1.getView() + 1); //리뷰 조회할때마다 조회수 증가
			reviewRepository.save(review1);
			return review1;
		} else {
			throw new IllegalArgumentException("존재하지 않는 reviewId");
		}
	}
}
