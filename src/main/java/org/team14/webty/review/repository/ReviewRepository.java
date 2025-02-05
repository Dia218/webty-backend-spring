package org.team14.webty.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team14.webty.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByUser_UserId(Long userId); // 특정 사용자의 리뷰 목록 조회

	// 조회수 내림차순으로 모든 리뷰 조회
	List<Review> findAllOrderByViewCountDesc();

	Long countByUser_UserId(Long userId); // 특정 사용자의 리뷰 개수 조회

	Page<Review> findByUser_UserId(Long userId, Pageable pageable); // 특정 사용자의 리뷰 목록 조회 (페이징 처리)
}
