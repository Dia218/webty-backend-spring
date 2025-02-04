package org.team14.webty.webtoon.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.security.authentication.AuthWebtyUserProvider;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.dto.FavoriteDto;
import org.team14.webty.webtoon.dto.WebtoonResponseDto;
import org.team14.webty.webtoon.entity.Favorite;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.mapper.FavoriteMapper;
import org.team14.webty.webtoon.mapper.WebtoonApiResponseMapper;
import org.team14.webty.webtoon.repository.FavoriteRepository;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final WebtoonRepository webtoonRepository;
	private final AuthWebtyUserProvider authWebtyUserProvider;

	@Transactional
	public void addFavorite(WebtyUserDetails webtyUserDetails, FavoriteDto favoriteDto) {
		WebtyUser webtyUser = authWebtyUserProvider.getAuthenticatedWebtyUser(webtyUserDetails);

		Webtoon webtoon = webtoonRepository.findById(favoriteDto.getWebtoonId())
			.orElseThrow(() -> new IllegalArgumentException("웹툰이 존재하지 않습니다."));

		if (favoriteRepository.findByWebtyUserAndWebtoon(webtyUser, webtoon).isPresent()) {
			throw new IllegalArgumentException("이미 관심 웹툰으로 등록되었습니다.");
		}

		favoriteRepository.save(FavoriteMapper.toEntity(webtyUser, webtoon));
	}

	@Transactional
	public void deleteFavorite(WebtyUserDetails webtyUserDetails, FavoriteDto favoriteDto) {
		WebtyUser webtyUser = authWebtyUserProvider.getAuthenticatedWebtyUser(webtyUserDetails);

		Webtoon webtoon = webtoonRepository.findById(favoriteDto.getWebtoonId())
			.orElseThrow(() -> new IllegalArgumentException("웹툰이 존재하지 않습니다."));

		favoriteRepository.deleteByWebtyUserAndWebtoon(webtyUser, webtoon);
	}

	@Transactional(readOnly = true)
	public List<WebtoonResponseDto> getUserFavorites(WebtyUserDetails webtyUserDetails) {
		WebtyUser webtyUser = authWebtyUserProvider.getAuthenticatedWebtyUser(webtyUserDetails);

		return favoriteRepository.findByWebtyUser(webtyUser)
			.stream()
			.map(Favorite::getWebtoon)
			.map(WebtoonApiResponseMapper::toDto)
			.collect(Collectors.toList());
	}
}
