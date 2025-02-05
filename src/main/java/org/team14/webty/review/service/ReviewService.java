package org.team14.webty.review.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.team14.webty.common.exception.BusinessException;
import org.team14.webty.common.exception.ErrorCode;
import org.team14.webty.review.dto.FeedReviewDetailResponse;
import org.team14.webty.review.dto.FeedReviewResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.mapper.ReviewMapper;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.reviewComment.mapper.ReviewCommentMapper;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.security.authentication.AuthWebtyUserProvider;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final WebtoonRepository webtoonRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final AuthWebtyUserProvider authWebtyUserProvider;

	// 리뷰 상세 조회
	@Transactional(readOnly = true)
	public FeedReviewDetailResponse getFeedReview(Long id, @RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
		Page<ReviewComment> comments = reviewCommentRepository.findAllByReviewIdOrderByDepthAndCommentId(id, pageable);
		Page<CommentResponse> commentResponses = comments.map(ReviewCommentMapper::toResponse);
		review.plusViewCount(); // 조회수 증가
		return ReviewMapper.toDetail(review, commentResponses);
	}

	// 전체 리뷰 조회
	@Transactional(readOnly = true)
	public Page<FeedReviewResponse> getAllFeedReviews(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		// 모든 리뷰 조회
		Page<Review> reviews = reviewRepository.findAll(pageable);

		// 모든 리뷰 ID 리스트 추출
		List<Long> reviewIds = reviews.stream().map(Review::getReviewId).toList();

		// 리뷰 ID 리스트를 기반으로 한 번의 쿼리로 모든 댓글 조회
		Map<Long, List<CommentResponse>> commentMap = getreviewMap(
			reviewIds);

		return reviews.map(review ->
			ReviewMapper.toResponse(review,
				commentMap.getOrDefault(review.getReviewId(), Collections.emptyList()))
		);
	}

	// 리뷰 생성
	public Long createFeedReview(WebtyUserDetails webtyUserDetails, ReviewRequest reviewRequest) {
		WebtyUser webtyUser = getAuthenticatedUser(webtyUserDetails);

		Webtoon webtoon = webtoonRepository.findById(reviewRequest.getWebtoonId())
			.orElseThrow(() -> new BusinessException(ErrorCode.WEBTOON_NOT_FOUND));

		Review review = ReviewMapper.toEntity(reviewRequest, webtyUser, webtoon);
		reviewRepository.save(review);
		return review.getReviewId();
	}

	// 리뷰 삭제
	public void deleteFeedReview(WebtyUserDetails webtyUserDetails, Long id) {
		WebtyUser webtyUser = getAuthenticatedUser(webtyUserDetails);

		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		if (!review.getUser().getUserId().equals(webtyUser.getUserId())) {
			throw new BusinessException(ErrorCode.REVIEW_PERMISSION_DENIED);
		}

		// 해당 리뷰에 달린 댓글 삭제 처리
		reviewCommentRepository.deleteAll(reviewCommentRepository.findAllByReviewIdOrderByParentCommentIdAndDepth(id));
		reviewRepository.delete(review);
	}

	// 리뷰 수정
	public Long updateFeedReview(WebtyUserDetails webtyUserDetails, Long id,
		ReviewRequest reviewRequest) {
		WebtyUser webtyUser = getAuthenticatedUser(webtyUserDetails);

		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		Webtoon webtoon = webtoonRepository.findById(reviewRequest.getWebtoonId())
			.orElseThrow(() -> new BusinessException(ErrorCode.WEBTOON_NOT_FOUND));

		if (!review.getUser().getUserId().equals(webtyUser.getUserId())) {
			throw new BusinessException(ErrorCode.REVIEW_PERMISSION_DENIED);
		}

		review.updateReview(reviewRequest.getTitle(), reviewRequest.getContent(), reviewRequest.getSpoilerStatus(),
			webtoon);
		reviewRepository.save(review);

		return review.getReviewId();
	}

	// 특정 사용자의 리뷰 목록 조회
	public List<FeedReviewResponse> getReviewsByUser(WebtyUserDetails webtyUserDetails) {
		WebtyUser webtyUser = getAuthenticatedUser(webtyUserDetails);

		List<Review> reviews = reviewRepository.findReviewByWebtyUser(webtyUser);
		List<Long> reviewIds = reviews.stream().map(Review::getReviewId).toList();
		List<ReviewComment> reviewComments = reviewCommentRepository.findAllByReviewIds(reviewIds);

		return reviews.stream()
			.map(review -> {
				List<CommentResponse> comments = reviewComments.stream()
					.filter(comment -> comment.getReview().getReviewId().equals(review.getReviewId()))
					.map(ReviewCommentMapper::toResponse)
					.collect(Collectors.toList());
				return ReviewMapper.toResponse(review, comments);
			})
			.collect(Collectors.toList());
	}

	// 조회수 내림차순으로 모든 리뷰 조회
	@Transactional(readOnly = true)
	public Page<FeedReviewResponse> getAllReviewsOrderByViewCountDesc(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderByViewCountDesc(pageable);

		// 모든 리뷰 ID 리스트 추출
		List<Long> reviewIds = reviews.stream().map(Review::getReviewId).toList();

		// 리뷰 ID 리스트를 기반으로 한 번의 쿼리로 모든 댓글 조회
		Map<Long, List<CommentResponse>> commentMap = getreviewMap(
			reviewIds);

		return reviews.map(review ->
			ReviewMapper.toResponse(review,
				commentMap.getOrDefault(review.getReviewId(), Collections.emptyList()))
		);
	}

	// 특정 사용자의 리뷰 개수 조회
	@Transactional(readOnly = true)
	public Long getReviewCountByUser(WebtyUserDetails webtyUserDetails) {
		WebtyUser webtyUser = getAuthenticatedUser(webtyUserDetails);
		return reviewRepository.countReviewByWebtyUser(webtyUser);
	}

	private Map<Long, List<CommentResponse>> getreviewMap(List<Long> reviewIds) {
		List<ReviewComment> allComments = reviewCommentRepository.findAllByReviewIds(reviewIds);

		// 리뷰 ID를 기준으로 댓글을 매핑하는 Map 생성
		Map<Long, List<CommentResponse>> commentMap = allComments.stream()
			.collect(Collectors.groupingBy(
				comment -> comment.getReview().getReviewId(),
				Collectors.mapping(ReviewCommentMapper::toResponse, Collectors.toList())
			));
		return commentMap;
	}

	public WebtyUser getAuthenticatedUser(WebtyUserDetails webtyUserDetails) {
		return authWebtyUserProvider.getAuthenticatedWebtyUser(webtyUserDetails);
	}
}
