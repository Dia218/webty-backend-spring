package org.team14.webty.webtoon.dto;

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
	private String authors;
}
