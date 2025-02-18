package org.team14.webty.voting.controller;

import static org.hamcrest.collection.IsCollectionWithSize.*;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.voting.entity.Similar;
import org.team14.webty.webtoon.entity.Webtoon;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
@Import(VotingTestDataInitializer.class)
class SimilarControllerTest {
	private final String similarPath = "/similar";
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private VotingTestDataInitializer votingTestDataInitializer;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;
	private Webtoon testTargetWebtoon;
	private Webtoon testChoiceWebtoon;

	@BeforeEach
	void beforeEach() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		votingTestDataInitializer.deleteAllData();
		testUser = votingTestDataInitializer.initTestUser();
		testTargetWebtoon = votingTestDataInitializer.newTestTargetWebtoon(1);
		testChoiceWebtoon = votingTestDataInitializer.newTestChoiceWebtoon(1);
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

		mockMvc.perform(post(similarPath)
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.similarThumbnailUrl").value(testChoiceWebtoon.getThumbnailUrl()))
			.andExpect(jsonPath("$.similarWebtoonId").value(testChoiceWebtoon.getWebtoonId()));
	}

	@Test
	@DisplayName("유사 삭제 테스트")
	void deleteSimilar_test() throws Exception {
		Similar testSimilar = votingTestDataInitializer.newTestSimilar(testUser, testTargetWebtoon, testChoiceWebtoon);

		mockMvc.perform(delete(similarPath + "/" + testSimilar.getSimilarId())
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("유사 목록 조회 테스트")
	void getSimilarList_test() throws Exception {
		Webtoon testChoiceWebtoon2 = votingTestDataInitializer.newTestChoiceWebtoon(2);
		votingTestDataInitializer.newTestSimilar(testUser, testTargetWebtoon, testChoiceWebtoon);
		votingTestDataInitializer.newTestSimilar(testUser, testTargetWebtoon, testChoiceWebtoon2);

		mockMvc.perform(get(similarPath)
				.param("targetWebtoonId", testTargetWebtoon.getWebtoonId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].similarWebtoonId").value(testChoiceWebtoon.getWebtoonId()))
			.andExpect(jsonPath("$.content[1].similarWebtoonId").value(testChoiceWebtoon2.getWebtoonId()));
	}
}