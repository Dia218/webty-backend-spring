package org.team14.webty.voting.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.voting.entity.Similar;
import org.team14.webty.voting.repository.SimilarRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
class SimilarControllerTest {
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebtoonRepository webtoonRepository;
	@Autowired
	private SimilarRepository similarRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;
	private Webtoon testTargetWebtoon;
	private Webtoon testChoiceWebtoon;

	@BeforeEach
	void beforeEach() {

		webtoonRepository.deleteAll();
		similarRepository.deleteAll();
		userRepository.deleteAll();

		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		testUser = userRepository.save(WebtyUser.builder()
			.nickname("유사웹툰등록자")
			.profileImage("similarTestImg")
			.socialProvider(SocialProvider.builder()
				.provider(SocialProviderType.KAKAO)
				.providerId("123456789")
				.build())
			.build());

		testTargetWebtoon = webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 투표 대상 웹툰")
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.testTargetWebtoon")
			.thumbnailUrl("testTargetWebtoon.jpg")
			.authors("testTargetWebtoonAuthor")
			.finished(true)
			.build());

		testChoiceWebtoon = webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 선택 대상 웹툰")
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.testChoiceWebtoon")
			.thumbnailUrl("testChoiceWebtoon.jpg")
			.authors("testChoiceWebtoonAuthor")
			.finished(true)
			.build());
	}

	@Test
	@DisplayName("유사 등록 테스트")
	void createSimilar_test() throws Exception {
		Long testTargetWebtoonId = testTargetWebtoon.getWebtoonId();
		Long testChoiceWebtoonId = testChoiceWebtoon.getWebtoonId();

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Long> requestBody = new HashMap<>();
		requestBody.put("targetWebtoonId", testTargetWebtoonId);
		requestBody.put("choiceWebtoonId", testChoiceWebtoonId);
		String jsonRequest = objectMapper.writeValueAsString(requestBody);

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/similar/create")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.similarThumbnailUrl").value(testChoiceWebtoon.getThumbnailUrl()))
			.andExpect(jsonPath("$.similarWebtoonId").value(testChoiceWebtoon.getWebtoonId()));
	}

	@Test
	@DisplayName("유사 등록 테스트")
	void deleteSimilar_test() throws Exception {
		Similar testSimilar = similarRepository.save(Similar.builder()
			.similarWebtoonId(testChoiceWebtoon.getWebtoonId())
			.similarResult(0L)
			.userId(testUser.getUserId())
			.targetWebtoon(testTargetWebtoon)
			.build());

		// 작성 예정
	}
}