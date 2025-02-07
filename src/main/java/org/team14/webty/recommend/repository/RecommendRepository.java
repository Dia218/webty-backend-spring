package org.team14.webty.recommend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team14.webty.recommend.entity.Recommend;
import org.team14.webty.recommend.enumerate.LikeType;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
	boolean existsByReviewIdAndUserIdAndLikeType(Long reviewId, Long userId, LikeType likeType);
	Optional<Recommend> findByReviewIdAndUserIdAndLikeType(Long reviewId, Long userId, LikeType likeType);
}
