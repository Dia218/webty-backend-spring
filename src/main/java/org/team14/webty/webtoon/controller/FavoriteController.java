package org.team14.webty.webtoon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.dto.FavoriteDto;
import org.team14.webty.webtoon.dto.WebtoonResponseDto;
import org.team14.webty.webtoon.service.FavoriteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping("/add")
	public ResponseEntity<Void> add(@AuthenticationPrincipal WebtyUser webtyUser,
		@RequestBody FavoriteDto favoriteDto) {
		Long userId = webtyUser.getUserId();
		favoriteService.addFavorite(userId, favoriteDto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> delete(@AuthenticationPrincipal WebtyUser webtyUser,
		@RequestBody FavoriteDto favoriteDto) {
		Long userId = webtyUser.getUserId();
		favoriteService.deleteFavorite(userId, favoriteDto);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/list")
	public ResponseEntity<List<WebtoonResponseDto>> getUserFavorite(@AuthenticationPrincipal WebtyUser webtyUser) {
		Long userId = webtyUser.getUserId();
		List<WebtoonResponseDto> userFavorites = favoriteService.getUserFavorites(userId);
		return ResponseEntity.ok().body(userFavorites);
	}
}
