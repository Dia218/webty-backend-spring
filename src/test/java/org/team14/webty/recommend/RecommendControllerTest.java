package org.team14.webty.recommend;

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
import org.springframework.http.MediaType;
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
public class RecommendControllerTest {

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

		Webtoon testWebtoon = webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 웹툰")
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.abc")
			.thumbnailUrl("www.bcd")
			.authors("testtest")
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
	@DisplayName("추천 테스트")
	void t1() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
				.header("Authorization", "Bearer " + accessToken)
				.param("type", type)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("추천 중복 테스트")
	void t2() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
			.header("Authorization", "Bearer " + accessToken)
			.param("type", type));

		mockMvc.perform(post("/recommend/" + reviewId)
				.header("Authorization", "Bearer " + accessToken)
				.param("type", type))
			.andExpect(status().isBadRequest())  // 400 상태
			.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 확인
			.andExpect(jsonPath("$.message").value("추천/비추천을 두번 이상 할 수 없습니다."))
			.andExpect(jsonPath("$.errorCode").value("RECOMMEND-001"))
			.andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));
	}

	@Test
	@DisplayName("추천 테스트 with 이상한 type")
	void t3() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "abb";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
				.header("Authorization", "Bearer " + accessToken)
				.param("type", type))
			.andExpect(status().isBadRequest())  // 400 상태
			.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 확인
			.andExpect(jsonPath("$.message").value("type은 LIKE(like), HATE(hate)만 가능합니다."))
			.andExpect(jsonPath("$.errorCode").value("RECOMMEND-002"))
			.andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));
	}

	@Test
	@DisplayName("추천 취소 테스트")
	void t4() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
			.header("Authorization", "Bearer " + accessToken)
			.param("type", type));

		mockMvc.perform(delete("/recommend/" + reviewId)
				.header("Authorization", "Bearer " + accessToken)
				.param("type", type))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("추천 취소 테스트 with 존재하지 않는 추천")
	void t5() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(delete("/recommend/" + reviewId)
				.header("Authorization", "Bearer " + accessToken)
				.param("type", type))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("해당 추천/비추천이 존재하지 않습니다."))
			.andExpect(jsonPath("$.errorCode").value("RECOMMEND-003"))
			.andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"));
	}

	@Test
	@DisplayName("로그인한 사용자 추천 리뷰 목록 조회")
	void t6() throws Exception {
		Long userId = testUser.getUserId();
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
			.header("Authorization", "Bearer " + accessToken)
			.param("type", type));

		mockMvc.perform(get("/recommend/user/" + userId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].reviewId").isNumber())
			.andExpect(jsonPath("$.content[0].title").isString())
			.andExpect(jsonPath("$.content[0].recommendCount").isNumber())
			.andExpect(jsonPath("$.currentPage").isNumber())
			.andExpect(jsonPath("$.totalPages").isNumber())
			.andExpect(jsonPath("$.totalElements").isNumber())
			.andExpect(jsonPath("$.hasNext").isBoolean())
			.andExpect(jsonPath("$.hasPrevious").isBoolean())
			.andExpect(jsonPath("$.isLast").isBoolean());
	}

	@Test
	@DisplayName("선택 리뷰 추천 상태")
	void t7() throws Exception {
		Long reviewId = testReview.getReviewId();
		String type = "like";
		String accessToken = jwtManager.createAccessToken(testUser.getUserId());

		mockMvc.perform(post("/recommend/" + reviewId)
			.header("Authorization", "Bearer " + accessToken)
			.param("type", type));

		mockMvc.perform(get("/recommend/" + reviewId + "/recommendation")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(jsonPath("$.LIKES").isBoolean())
			.andExpect(jsonPath("$.LIKES").value(true))
			.andExpect(jsonPath("$.HATES").isBoolean())
			.andExpect(jsonPath("$.HATES").value(false));
	}
}