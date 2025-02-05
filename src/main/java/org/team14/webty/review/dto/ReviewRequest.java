package org.team14.webty.review.dto;

import org.team14.webty.review.enumrate.SpoilerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
	private Long webtoonId;
	private String content;
	private String title;
	private SpoilerStatus spoilerStatus;
}
