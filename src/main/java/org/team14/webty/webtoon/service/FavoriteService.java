package org.team14.webty.webtoon.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;
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
	private final UserRepository userRepository;
	private final WebtoonRepository webtoonRepository;

	@Transactional
	public void addFavorite(Long userId, FavoriteDto favoriteDto) {
		WebtyUser user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

		Webtoon webtoon = webtoonRepository.findById(favoriteDto.getWebtoonId())
			.orElseThrow(() -> new IllegalArgumentException("웹툰이 존재하지 않습니다."));

		if (favoriteRepository.findByWebtyUserAndWebtoon(user, webtoon).isPresent()) {
			throw new IllegalArgumentException("이미 관심 웹툰으로 등록되었습니다.");
		}

		Favorite favorite = FavoriteMapper.toEntity(user, webtoon);
		favoriteRepository.save(favorite);
	}

	@Transactional
	public void deleteFavorite(Long userId, FavoriteDto favoriteDto) {
		WebtyUser user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

		Webtoon webtoon = webtoonRepository.findById(favoriteDto.getWebtoonId())
			.orElseThrow(() -> new IllegalArgumentException("웹툰이 존재하지 않습니다."));

		favoriteRepository.deleteByWebtyUserAndWebtoon(user, webtoon);
	}

	@Transactional(readOnly = true)
	public List<WebtoonResponseDto> getUserFavorites(Long userId) {
		WebtyUser user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("유저가 존재하지 않습니다."));

		return favoriteRepository.findByWebtyUser(user)
			.stream()
			.map(Favorite::getWebtoon)
			.map(WebtoonApiResponseMapper::toDto)
			.collect(Collectors.toList());
	}
}
