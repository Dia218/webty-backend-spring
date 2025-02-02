package org.team14.webty.webtoon.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.team14.webty.webtoon.api.WebtoonPageApiResponse;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.mapper.WebtoonApiResponseMapper;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebtoonService {
	private static final String URL_QUERY_TEMPLATE = "https://korea-webtoon-api-cc7dda2f0d77.herokuapp.com/webtoons?page=%s&perPage=%s&sort=%s&provider=%s";
	private static final int DEFAULT_PAGE_SIZE = 100;
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final String DEFAULT_SORT = "ASC";

	private final WebtoonRepository webtoonRepository;
	private final RestTemplate restTemplate;

	@Transactional
	public void saveWebtoons() {
		for (Platform provider : Platform.values()) {
			try {
				saveWebtoonsByProvider(provider);
			} catch (Exception e) {
				log.error("웹툰 저장 중 오류 발생 - Provider: {}, Error: {}", provider, e.getMessage(), e);
			}
		}
		log.info("모든 데이터 저장 완료");
	}

	private void saveWebtoonsByProvider(Platform provider) {
		boolean isLastPage = false;
		int page = DEFAULT_PAGE_NUMBER;

		do {
			log.info(String.valueOf(page));
			WebtoonPageApiResponse webtoonPageApiResponse = getWebtoonPageApiResponse(page, DEFAULT_PAGE_SIZE, DEFAULT_SORT, provider);
			saveWebtoonsFromPage(webtoonPageApiResponse);
			isLastPage = webtoonPageApiResponse.isLastPage();
			page++;
		} while (!isLastPage);
	}

	private WebtoonPageApiResponse getWebtoonPageApiResponse(int page, int perPage, String sort, Platform provider) {
		String url = String.format(URL_QUERY_TEMPLATE, page, perPage, sort, provider.getPlatformName());
		log.info(url);
		try {
			return restTemplate.getForObject(url, WebtoonPageApiResponse.class);
		} catch (RestClientException e) {
			log.error("API 요청 실패 - URL: {}, Error: {}", url, e.getMessage(), e);
			return null;
		}
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

	public Webtoon findWebtoon(Long id) {
		return webtoonRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 웹툰 ID 입니다."));
	}
}
