package org.team14.webty.webtoon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.webtoon.service.WebtoonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WebtoonController {
	private final WebtoonService webtoonService;

	@GetMapping("/fetch-webtoons")
	public void fetchWebtoons() {
		webtoonService.saveWebtoons();
	}
}
