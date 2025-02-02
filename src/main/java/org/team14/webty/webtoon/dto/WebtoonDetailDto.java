package org.team14.webty.webtoon.dto;

import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;

import lombok.Getter;

@Getter
public class WebtoonDetailDto {
	private Long webtoonId;
	private String webtoonName;
	private Platform platform;
	private String webtoonLink;
	private String thumbnailUrl;
	private String authors;
	private boolean finished;

	public WebtoonDetailDto(Webtoon webtoon){
		this.webtoonId=webtoon.getWebtoonId();
		this.webtoonName=webtoon.getWebtoonName();
		this.platform=webtoon.getPlatform();
		this.webtoonLink=webtoon.getWebtoonLink();
		this.thumbnailUrl=webtoon.getThumbnailUrl();
		this.authors=webtoon.getAuthors();
		this.finished= webtoon.isFinished();
	}
}
