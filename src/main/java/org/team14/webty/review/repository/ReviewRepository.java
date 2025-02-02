package org.team14.webty.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team14.webty.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
