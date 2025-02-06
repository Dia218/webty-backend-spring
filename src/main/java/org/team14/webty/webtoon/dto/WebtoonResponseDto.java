package org.team14.webty.webtoon.dto;

import org.team14.webty.webtoon.enumerate.Platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebtoonResponseDto {
	private Long webtoonId;
	private String webtoonName;
	private Boolean isFinished;
	private String thumbnailUrl;
	private Platform platform;
	private String authors;
}
