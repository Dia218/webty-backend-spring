package org.team14.webty.recommend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.common.exception.BusinessException;
import org.team14.webty.common.exception.ErrorCode;
import org.team14.webty.recommend.entity.Recommend;
import org.team14.webty.recommend.enumerate.LikeType;
import org.team14.webty.recommend.repository.RecommendRepository;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.user.entity.WebtyUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendService {
	private final ReviewRepository reviewRepository;
	private final RecommendRepository recommendRepository;

	@Transactional
	public Long createRecommend(WebtyUserDetails webtyUserDetails, Long reviewId, String type) {
		WebtyUser webtyUser = webtyUserDetails.getWebtyUser();

		reviewRepository.findById(reviewId)
			.orElseThrow(()->new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		if (recommendRepository.existsByReviewIdAndUserIdAndLikeType(
			reviewId, webtyUser.getUserId(), LikeType.fromString(type))) {
			throw new BusinessException(ErrorCode.RECOMMEND_DUPLICATION_ERROR);
		}

		Recommend recommend = Recommend.builder()
			.likeType(LikeType.fromString(type))
			.userId(webtyUser.getUserId())
			.reviewId(reviewId)
			.build();

		recommendRepository.save(recommend);
		return recommend.getVoteId();
	}
}
