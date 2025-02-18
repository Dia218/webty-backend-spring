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
import org.team14.webty.voting.entity.Vote;
import org.team14.webty.voting.enumerate.VoteType;
import org.team14.webty.webtoon.entity.Webtoon;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
@Import(VotingTestDataInitializer.class)
class VoteControllerTest {
	private final String votePath = "/vote";
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private VotingTestDataInitializer votingTestDataInitializer;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;
	private Similar testSimilar;

	@BeforeEach
	void beforeEach() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		votingTestDataInitializer.deleteAllData();
		testUser = votingTestDataInitializer.initTestUser();
		Webtoon testTargetWebtoon = votingTestDataInitializer.newTestTargetWebtoon(1);
		Webtoon testChoiceWebtoon = votingTestDataInitializer.newTestChoiceWebtoon(1);
		testSimilar = votingTestDataInitializer.newTestSimilar(testUser, testTargetWebtoon, testChoiceWebtoon);
	}

	@Test
	@DisplayName("투표 등록 테스트")
	void vote_test() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("similarId", String.valueOf(testSimilar.getSimilarId()));
		requestBody.put("voteType", "agree");
		String jsonRequest = objectMapper.writeValueAsString(requestBody);

		mockMvc.perform(post(votePath)
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("투표 취소 테스트")
	void cancel_test() throws Exception {
		Vote testVote = votingTestDataInitializer.newTestVote(testUser, testSimilar, VoteType.AGREE);

		mockMvc.perform(delete(votePath + "/" + testVote.getVoteId())
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}
}