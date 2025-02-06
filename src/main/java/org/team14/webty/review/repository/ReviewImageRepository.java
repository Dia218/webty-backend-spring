package org.team14.webty.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.entity.ReviewImage;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	List<ReviewImage> findAllByReview(Review review);
}
