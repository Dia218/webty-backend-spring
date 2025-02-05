package org.team14.webty.review.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.team14.webty.review.dto.FeedReviewDetailResponse;
import org.team14.webty.review.dto.FeedReviewResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.entity.Review;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.entity.Webtoon;

public class ReviewMapper {
	public static Review toEntity(ReviewRequest request, WebtyUser webtyUser, Webtoon webtoon) {
		return Review.builder()
			.user(webtyUser)
			.isSpoiler(request.getSpoilerStatus())
			.content(request.getContent())
			.title(request.getTitle())
			.viewCount(0)
			.webtoon(webtoon)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public static FeedReviewResponse toResponse(Review review, List<CommentResponse> comments) {
		return FeedReviewResponse.builder()
			.reviewId(review.getReviewId())
			.userDataResponse(new UserDataResponse(review.getUser()))
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.spoilerStatus(review.getIsSpoiler())
			.webtoonId(review.getWebtoon().getWebtoonId())
			.thumbnailUrl(review.getWebtoon().getThumbnailUrl())
			.commentCount(comments.size()) // 댓글 개수만
			.build();
	}

	public static FeedReviewDetailResponse toDetail(Review review, Page<CommentResponse> comments) {
		return FeedReviewDetailResponse.builder()
			.reviewId(review.getReviewId())
			.userDataResponse(new UserDataResponse(review.getUser()))
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.spoilerStatus(review.getIsSpoiler())
			.thumbnailUrl(review.getWebtoon().getThumbnailUrl())
			.commentResponses(comments) // 댓글 정보까지
			.build();
	}
}
