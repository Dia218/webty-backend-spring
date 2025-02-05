package org.team14.webty.review.dto;

import org.team14.webty.review.entity.Review;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.entity.Webtoon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
	private Long reviewId;
	private WebtyUser user; // 사용자 프로필, 닉네임
	private String content;
	private String title;
	private Integer viewCount;
	private Review.SpoilerStatus isSpoiler;
	private String webtoonId; // 웹툰 이미지?
	private ReviewComment reviewComment;
	private Webtoon webtoon;
}
