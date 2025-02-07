package org.team14.webty.review.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.team14.webty.common.dto.PageDto;
import org.team14.webty.review.dto.ReviewDetailResponse;
import org.team14.webty.review.dto.ReviewItemResponse;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.entity.ReviewImage;
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

	public static ReviewItemResponse toResponse(Review review, List<CommentResponse> comments,
		List<String> imageUrls) {
		return ReviewItemResponse.builder()
			.reviewId(review.getReviewId())
			.userDataResponse(new UserDataResponse(review.getUser()))
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.spoilerStatus(review.getIsSpoiler())
			.webtoonId(review.getWebtoon().getWebtoonId())
			.webtoonName(review.getWebtoon().getWebtoonName())
			.thumbnailUrl(review.getWebtoon().getThumbnailUrl())
			.imageUrls(imageUrls)
			.commentCount(comments.size()) // 댓글 개수만
			.build();
	}

	public static ReviewDetailResponse toDetail(Review review, PageDto<CommentResponse> comments,
		List<ReviewImage> reviewImages) {
		return ReviewDetailResponse.builder()
			.reviewId(review.getReviewId())
			.userDataResponse(new UserDataResponse(review.getUser()))
			.content(review.getContent())
			.title(review.getTitle())
			.viewCount(review.getViewCount())
			.spoilerStatus(review.getIsSpoiler())
			.thumbnailUrl(review.getWebtoon().getThumbnailUrl())
			.imageUrls(reviewImages.stream().map(ReviewImage::getImageUrl).toList())
			.commentResponses(comments) // 댓글 정보까지
			.build();
	}

	public static ReviewImage toImageEntity(String imageUrl, Review review) {
		return ReviewImage.builder()
			.imageUrl(imageUrl)
			.review(review)
			.build();
	}
}
