package org.team14.webty.webtoon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.webtoon.dto.WebtoonDetailDto;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.mapper.WebtoonDetailMapper;
import org.team14.webty.webtoon.service.WebtoonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webtoons")
public class WebtoonController {
	private final WebtoonService webtoonService;

	@GetMapping("/fetch")
	public void fetchWebtoons() {
		webtoonService.saveWebtoons();
	}

	@GetMapping("/{id}")
	public ResponseEntity<WebtoonDetailDto> findWebtoon(@Valid @Min(1) @PathVariable Long id){
		Webtoon webtoon = webtoonService.findWebtoon(id);
		return ResponseEntity.ok(WebtoonDetailMapper.toDto(webtoon));
	}
}
