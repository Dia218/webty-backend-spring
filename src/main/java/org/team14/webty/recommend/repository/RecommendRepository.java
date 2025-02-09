package org.team14.webty.recommend.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team14.webty.recommend.entity.Recommend;
import org.team14.webty.recommend.enumerate.LikeType;
import org.team14.webty.review.entity.Review;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
	boolean existsByReviewAndUserIdAndLikeType(Review review, Long userId, LikeType likeType);
	Optional<Recommend> findByReviewAndUserIdAndLikeType(Review review, Long userId, LikeType likeType);

	@Query("SELECT new map( " +
		"SUM(CASE WHEN r.likeType = 'LIKE' THEN 1 ELSE 0 END) AS likes, " +
		"SUM(CASE WHEN r.likeType = 'HATE' THEN 1 ELSE 0 END) AS hates) " +
		"FROM Recommend r WHERE r.review.reviewId = :reviewId")
	Map<String, Long> getRecommendCounts(@Param("reviewId") Long reviewId);

	// 쿼리 7개 (JOIN FETCH 사용 X)
	// @Query("""
    //     SELECT r FROM Review r
    //     JOIN Recommend rec ON r.reviewId = rec.review.reviewId
    //     WHERE rec.userId = :userId
    //     ORDER BY rec.voteId DESC
    // """)
	// Page<Review> getUserRecommendReview(@Param("userId") Long userId, Pageable pageable);

	// 쿼리 5개 (JOIN FETCH 사용 O)
	@Query("""
    SELECT r FROM Review r
    JOIN FETCH r.user
    JOIN FETCH r.webtoon
    WHERE r.reviewId IN (SELECT rec.review.reviewId FROM Recommend rec WHERE rec.userId = :userId)
    ORDER BY (SELECT MAX(rec.voteId) FROM Recommend rec WHERE rec.review.reviewId = r.reviewId) DESC
""")
	Page<Review> getUserRecommendReview(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT COALESCE(COUNT(r.voteId), 0) " +
		"FROM Review rv LEFT JOIN Recommend r ON rv.reviewId = r.review.reviewId AND r.likeType = 'LIKE' " +
		"WHERE rv.reviewId IN :reviewIds " +
		"GROUP BY rv.reviewId ORDER BY rv.reviewId")
	List<Long> getLikeCounts(@Param("reviewIds") List<Long> reviewIds);
}
