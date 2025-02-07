package org.team14.webty.recommend.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team14.webty.recommend.entity.Recommend;
import org.team14.webty.recommend.enumerate.LikeType;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
	boolean existsByReviewIdAndUserIdAndLikeType(Long reviewId, Long userId, LikeType likeType);
	Optional<Recommend> findByReviewIdAndUserIdAndLikeType(Long reviewId, Long userId, LikeType likeType);

	@Query("SELECT new map( " +
		"SUM(CASE WHEN r.likeType = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
		"SUM(CASE WHEN r.likeType = 'HATE' THEN 1 ELSE 0 END) AS hates) " +
		"FROM Recommend r WHERE r.reviewId = :reviewId")
	Map<String, Long> getRecommendCounts(@Param("reviewId") Long reviewId);
}
