package org.team14.webty.webtoon.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.team14.webty.recommend.repository.RecommendRepository;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.enumrate.SpoilerStatus;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
public class WebtoonControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WebtoonRepository webtoonRepository;
	@Autowired
	private RecommendRepository recommendRepository;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;
	private Review testReview;
	private Webtoon testWebtoon;
	private Webtoon testWebtoon2;

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

		testWebtoon2 = webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 웹툰")
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.abc")
			.thumbnailUrl("www.bcd")
			.authors("Author1")
			.finished(true)
			.build());

		testReview = reviewRepository.save(Review.builder()
			.user(testUser)
			.content("테스트 리뷰")
			.title("테스트 리뷰 제목")
			.viewCount(0)
			.isSpoiler(SpoilerStatus.FALSE)
			.webtoon(testWebtoon)
			.createdAt(LocalDateTime.now())
			.build());
	}

	@AfterEach
	void afterEach() {
		recommendRepository.deleteAll();
		reviewRepository.deleteAll();
		webtoonRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("웹툰 조회 테스트")
	void t1() throws Exception {
		Long webtoonId = testWebtoon.getWebtoonId();
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(get("/webtoons/" + webtoonId)
				.header("Authorization", "Bearer " + accessToken)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("웹툰 검색 테스트- 파라미터(page,size)")
	void t2() throws Exception {
		// 테스트 데이터 준비
		int page = 0;
		int size = 10;
		//String sortBy = "name";
		//String sortDirection = "a";

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		String authors = "Author1";

		mockMvc.perform(get("/webtoons")
				.header("Authorization", "Bearer " + accessToken)
				//.param("authors", authors)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				//.param("sortBy", sortBy)
				//.param("sortDirection", sortDirection)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			//	응답의 "content" 필드가 배열인지 확인
			.andExpect(jsonPath("$.content.length()").value(2));
		// 예시로 빈 배열이 반환되도록 설정
	}

	@Test
	@DisplayName("웹툰 검색 테스트- 작가 검색")
	void t3() throws Exception {
		// 테스트 데이터 준비
		int page = 0;
		int size = 10;
		//String sortBy = "name";
		//String sortDirection = "a";

		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		String authors = "Author1";

		mockMvc.perform(get("/webtoons")
				.header("Authorization", "Bearer " + accessToken)
				.param("authors", authors)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				//.param("sortBy", sortBy)
				//.param("sortDirection", sortDirection)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			//	응답의 "content" 필드가 배열인지 확인
			.andExpect(jsonPath("$.content.length()").value(2));
		// 예시로 빈 배열이 반환되도록 설정
	}

}
