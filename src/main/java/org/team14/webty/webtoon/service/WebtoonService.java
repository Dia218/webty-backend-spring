package org.team14.webty.webtoon.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.team14.webty.webtoon.api.WebtoonPageApiResponse;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.mapper.WebtoonApiResponseMapper;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebtoonService {
	private static final String URL_QUERY_TEMPLATE = "https://korea-webtoon-api-cc7dda2f0d77.herokuapp.com/webtoons?page=%s&perPage=%s&sort=%s";
	private static final int DEFAULT_PAGE_SIZE = 30;
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final String DEFAULT_SORT = "ASC";

	private final WebtoonRepository webtoonRepository;
	private final RestTemplate restTemplate;

	public void saveWebtoons() {

		boolean isLastPage = false;
		int page = DEFAULT_PAGE_NUMBER;

		do {
			WebtoonPageApiResponse webtoonPageApiResponse = getWebtoonPageApiResponse(page, DEFAULT_PAGE_SIZE,
				DEFAULT_SORT);
			saveWebtoonsFromPage(webtoonPageApiResponse);
			isLastPage = webtoonPageApiResponse.isLastPage();
			page++;
		} while (!isLastPage);

		log.info("모든 데이터 저장완료");
	}

	private WebtoonPageApiResponse getWebtoonPageApiResponse(int page, int perPage, String sort) {
		String url = String.format(URL_QUERY_TEMPLATE, page, perPage, sort);
		return restTemplate.getForObject(url, WebtoonPageApiResponse.class);
	}

	private void saveWebtoonsFromPage(WebtoonPageApiResponse webtoonPageApiResponse) {

		List<Webtoon> webtoons = webtoonPageApiResponse.getWebtoonApiResponses()
			.stream()
			.map(WebtoonApiResponseMapper::toEntity)
			.collect(Collectors.toList());

		webtoonRepository.saveAll(webtoons);
	}

	public void updateWebtoons() {
		// 작성 필요
	}
}
