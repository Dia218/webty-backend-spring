package org.team14.webty.review.dto;

import org.springframework.data.domain.Page;
import org.team14.webty.review.enumrate.SpoilerStatus;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.user.dto.UserDataResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedReviewDetailResponse {
	private Long reviewId;
	private UserDataResponse userDataResponse; // 사용자 프로필, 닉네임
	private String content;
	private String title;
	private Integer viewCount;
	private SpoilerStatus spoilerStatus;
	private String thumbnailUrl;
	private Page<CommentResponse> commentResponses;
}
