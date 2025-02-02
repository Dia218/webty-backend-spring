package org.team14.webty.webtoon.dto;

import org.team14.webty.webtoon.entity.Webtoon;

public class WebtoonNameThumbnailDto {
	private Long webtoonId;
	private String webtoonName;
	private String thumbnailUrl;
	// 섬네일 클릭하면 이동되게 만들수도 있을것? 같아서 추가했습니다.
	// webty 페이지로 이동시킬거면 삭제해야할 것 같습니다.
	private String webtoonLink;

	public WebtoonNameThumbnailDto(Webtoon webtoon){
		this.webtoonId=webtoon.getWebtoonId();
		this.webtoonName=webtoon.getWebtoonName();
		this.webtoonLink=webtoon.getWebtoonLink();
		this.thumbnailUrl=webtoon.getThumbnailUrl();
	}
}
