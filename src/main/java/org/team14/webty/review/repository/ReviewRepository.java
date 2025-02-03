package org.team14.webty.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team14.webty.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
