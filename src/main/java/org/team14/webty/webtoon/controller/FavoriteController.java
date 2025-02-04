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
import org.team14.webty.security.authentication.WebtyUserDetails;
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
	public ResponseEntity<Void> add(@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@RequestBody FavoriteDto favoriteDto) {
		favoriteService.addFavorite(webtyUserDetails, favoriteDto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> delete(@AuthenticationPrincipal WebtyUserDetails webtyUserDetails,
		@RequestBody FavoriteDto favoriteDto) {
		favoriteService.deleteFavorite(webtyUserDetails, favoriteDto);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/list")
	public ResponseEntity<List<WebtoonResponseDto>> getUserFavorite(
		@AuthenticationPrincipal WebtyUserDetails webtyUserDetails) {
		List<WebtoonResponseDto> userFavorites = favoriteService.getUserFavorites(webtyUserDetails);
		return ResponseEntity.ok().body(userFavorites);
	}
}
