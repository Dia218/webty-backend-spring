package org.team14.webty.webtoon.mapper;

import java.util.List;

import org.team14.webty.webtoon.api.WebtoonApiResponse;
import org.team14.webty.webtoon.dto.WebtoonResponseDto;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;

public class WebtoonApiResponseMapper {
	public static Webtoon toEntity(WebtoonApiResponse webtoonApiResponse) {
		return Webtoon.builder()
			.webtoonName(webtoonApiResponse.getTitle())
			.platform(Platform.fromString(webtoonApiResponse.getProvider()))
			.webtoonLink(webtoonApiResponse.getUrl())
			.thumbnailUrl(webtoonApiResponse.getThumbnails().isEmpty() ? null :
				webtoonApiResponse.getThumbnails().getFirst())
			.authors(formatAuthors(webtoonApiResponse.getAuthors()))
			.finished(webtoonApiResponse.isEnd())
			.build();
	}

	public static WebtoonResponseDto toDto(Webtoon webtoon) {
		return WebtoonResponseDto.builder()
			.webtoonId(webtoon.getWebtoonId())
			.webtoonName(webtoon.getWebtoonName())
			.isFinished(webtoon.isFinished())
			.thumbnailUrl(webtoon.getThumbnailUrl())
			.platform(webtoon.getPlatform())
			.authors(webtoon.getAuthors())
			.build();
	}

	public static String formatAuthors(List<String> authors) {
		return authors == null ? "" : String.join(", ", authors);
	}
}
