package org.team14.webty.webtoon.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.mapper.FavoriteMapper;
import org.team14.webty.webtoon.repository.FavoriteRepository;
import org.team14.webty.webtoon.repository.WebtoonRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
class FavoriteControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WebtoonRepository webtoonRepository;
	@Autowired
	private FavoriteRepository favoriteRepository;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;
	private Review testReview;
	private Webtoon testWebtoon;

	@BeforeEach
	void beforeEach() {
		testUser = userRepository.save(WebtyUser.builder()
			.nickname("테스트유저")
			.profileImage("dasdsa")
			.socialProvider(SocialProvider.builder()
				.provider(SocialProviderType.KAKAO)
				.providerId("313213231")
				.build())
			.build());

		testWebtoon = webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 웹툰1")
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.abc")
			.thumbnailUrl("www.bcd")
			.authors("Author1")
			.finished(true)
			.build());
	}

	@AfterEach
	void afterEach() {
		favoriteRepository.deleteAll(); //참조 무결성 때문에 favorite DB 먼저 삭제
		webtoonRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("웹툰 추천 테스트")
	void t1() throws Exception {

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		Long webtoonId = testWebtoon.getWebtoonId();

		mockMvc.perform(post("/favorite/" + webtoonId)
				.header("Authorization", "Bearer " + accessToken)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("추천웹툰 취소 테스트")
	void t2() throws Exception {

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		favoriteRepository.save(FavoriteMapper.toEntity(testUser, testWebtoon));

		Long webtoonId = testWebtoon.getWebtoonId();

		mockMvc.perform(delete("/favorite/" + webtoonId)
				.header("Authorization", "Bearer " + accessToken)
				.with(csrf()))
			.andExpect(status().isOk());//1.상태코드 200ok인지 확인

		assertTrue(favoriteRepository.findByWebtyUserAndWebtoon(testUser, testWebtoon).isEmpty());
		//2. db에서 삭제됐는지 확인
	}

	@Test
	@DisplayName("유저의 추천웹툰 목록 테스트")
	void t3() throws Exception {

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		favoriteRepository.save(FavoriteMapper.toEntity(testUser, testWebtoon));

		mockMvc.perform(get("/favorite/list")
				.header("Authorization", "Bearer " + accessToken)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].webtoonName").value("테스트 웹툰1"));
	}

	@Test
	@DisplayName("유저가 추천웹툰으로 등록했는지 여부 테스트")
	void t4() throws Exception {
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());
		Long webtoonId = testWebtoon.getWebtoonId();

		favoriteRepository.save(FavoriteMapper.toEntity(testUser, testWebtoon));

		mockMvc.perform(get("/favorite/" + webtoonId)
				.header("Authorization", "Bearer " + accessToken)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("true"));
	}
}