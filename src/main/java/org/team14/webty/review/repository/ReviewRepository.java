package org.team14.webty.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.team14.webty.review.entity.Review;
import org.team14.webty.user.entity.WebtyUser;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	@Query("SELECT r FROM Review r WHERE r.user = :webtyUser")
	List<Review> findReviewByWebtyUser(WebtyUser webtyUser); // 특정 사용자의 리뷰 목록 조회

	@Query("SELECT r FROM Review r WHERE r.user =: webtyUser")
	Page<Review> findByWebtyUser(WebtyUser webtyUser, Pageable pageable); // 특정 사용자의 리뷰 목록 조회 (페이징 처리)
	
	// 조회수 내림차순으로 모든 리뷰 조회
	Page<Review> findAllByOrderByViewCountDesc(Pageable pageable);

	Page<Review> findAll(Pageable pageable); // 모든 리뷰 조회 (페이징 처리)

	@Query("SELECT COUNT(r) FROM Review r WHERE r.user = :webtyUser")
	Long countReviewByWebtyUser(WebtyUser webtyUser); // 특정 사용자의 리뷰 개수 조회

}
