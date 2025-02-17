package org.team14.webty.review.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.team14.webty.review.dto.ReviewRequest;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.enumrate.SpoilerStatus;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtManager jwtManager;

	private String jwtToken;
	private String jwtToken2;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private WebtoonRepository webtoonRepository;

	@BeforeEach
	void setUp() {
		if (!userRepository.existsById(1L)) {

			// 30명의 테스트 유저 생성
			List<WebtyUser> users = new ArrayList<>();
			for (int i = 1; i <= 30; i++) {
				WebtyUser testUser = userRepository.save(
					WebtyUser.builder()
						.nickname("testUser" + i)
						.profileImage("https://example.com/profile" + i + ".jpg")
						.build()
				);
				users.add(testUser);
			}

			// "Popular Webtoon" (1번 웹툰)
			List<Webtoon> webtoons = new ArrayList<>();
			Webtoon popularWebtoon = webtoonRepository.save(
				Webtoon.builder()
					.webtoonName("Popular Webtoon")
					.platform(Platform.NAVER_WEBTOON)
					.webtoonLink("https://example.com/popular-webtoon")
					.thumbnailUrl("https://example.com/popular-thumbnail.jpg")
					.authors("Famous Author")
					.finished(false)
					.build()
			);
			webtoons.add(popularWebtoon);

			for (int i = 2; i <= 10; i++) {
				Webtoon testWebtoon = webtoonRepository.save(
					Webtoon.builder()
						.webtoonName("Test Webtoon " + i)
						.platform(Platform.NAVER_WEBTOON)
						.webtoonLink("https://example.com/webtoon" + i)
						.thumbnailUrl("https://example.com/thumbnail" + i + ".jpg")
						.authors("Test Author " + i)
						.finished(i % 2 == 0)
						.build()
				);
				webtoons.add(testWebtoon);
			}

			// 유저 1번이 여러 개의 리뷰 작성 (5개)
			for (int i = 1; i <= 5; i++) {
				reviewRepository.save(
					Review.builder()
						.user(users.get(0)) // 유저 1번
						.webtoon(popularWebtoon) // 인기 웹툰에 몰아주기
						.title("Review by User 1 - " + i)
						.content("This is a review written by user 1.")
						.viewCount(i * 10)
						.createdAt(LocalDateTime.now().minusDays(i))
						.updatedAt(LocalDateTime.now())
						.build()
				);
			}

			// 나머지 유저(2~30)는 각각 한 개의 리뷰만 작성
			for (int i = 1; i < users.size(); i++) {
				reviewRepository.save(
					Review.builder()
						.user(users.get(i))
						.webtoon(webtoons.get(i % webtoons.size())) // 웹툰 분배
						.title("Review by User " + (i + 1))
						.content("This is a test review content by user " + (i + 1))
						.viewCount((i + 1) * 5)
						.createdAt(LocalDateTime.now().minusDays(i + 1))
						.updatedAt(LocalDateTime.now())
						.build()
				);
			}

			// 검색 테스트를 위한 리뷰 추가 (제목에 "Search" 포함)
			for (int i = 1; i <= 3; i++) {
				reviewRepository.save(
					Review.builder()
						.user(users.get(i % users.size()))
						.webtoon(webtoons.get(i % webtoons.size()))
						.title("Search Review " + i)
						.content("This review should appear in search results.")
						.viewCount(50 * i)
						.createdAt(LocalDateTime.now().minusDays(i))
						.updatedAt(LocalDateTime.now())
						.build()
				);
			}
		}
	}

	@BeforeEach
	void setUpAuthentication() {
		Long testUserId = 1L;
		jwtToken = "Bearer " + jwtManager.createAccessToken(testUserId);
		jwtToken2 = "Bearer " + jwtManager.createAccessToken(2L);
	}

	@Test
	@DisplayName("리뷰 세부조회")
	void t1() throws Exception {
		mockMvc.perform(get("/reviews/1"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 세부조회 Error 존재하지 않는 리뷰")
	void t2() throws Exception {
		mockMvc.perform(get("/reviews/53535355"))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-001"))
			.andExpect(jsonPath("$.message").value("리뷰를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("리뷰 전체조회")
	void t3() throws Exception {
		mockMvc.perform(get("/reviews"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 생성")
	void t4() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(1L)
			.title("New Review Title")
			.content("This is a new review content.")
			.spoilerStatus(SpoilerStatus.FALSE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		// 이미지 파일 추가
		MockMultipartFile imageFile = new MockMultipartFile(
			"images",
			"test-image.jpg",
			"image/jpeg",
			"image-data".getBytes()
		);

		mockMvc.perform(multipart("/reviews/create")
				.file(reviewRequestPart) // JSON 데이터 추가
				.file(imageFile) // 이미지 파일 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 생성 Error 존재하지 않는 웹툰")
	void t5() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(12233131213L)
			.title("New Review Title")
			.content("This is a new review content.")
			.spoilerStatus(SpoilerStatus.FALSE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/reviews/create")
				.file(reviewRequestPart) // JSON 데이터 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("WEBTOON-001"))
			.andExpect(jsonPath("$.message").value("웹툰을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("리뷰 삭제")
	void t6() throws Exception {
		mockMvc.perform(delete("/reviews/delete/1")
				.header(HttpHeaders.AUTHORIZATION, jwtToken))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 삭제 Error 존재하지 않는 리뷰")
	void t7() throws Exception {
		mockMvc.perform(delete("/reviews/delete/1313131311")
				.header(HttpHeaders.AUTHORIZATION, jwtToken))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-001"))
			.andExpect(jsonPath("$.message").value("리뷰를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("리뷰 삭제 Error 권한 없음")
	void t8() throws Exception {
		mockMvc.perform(delete("/reviews/delete/22")
				.header(HttpHeaders.AUTHORIZATION, jwtToken))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-002"))
			.andExpect(jsonPath("$.message").value("리뷰에 대한 삭제/수정 권한이 없습니다."));
	}

	@Test
	@DisplayName("리뷰 수정")
	void t9() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(1L)
			.title("Updated Review Title")
			.content("This is an updated review content.")
			.spoilerStatus(SpoilerStatus.TRUE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		// 이미지 파일 추가
		MockMultipartFile imageFile = new MockMultipartFile(
			"images",
			"updated-image.jpg",
			"image/jpeg",
			"updated-image-data".getBytes()
		);

		mockMvc.perform(multipart("/reviews/put/2")
				.file(reviewRequestPart) // JSON 데이터 추가
				.file(imageFile) // 이미지 파일 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(httprequest -> {
					httprequest.setMethod("PUT"); // multipart()는 기본적으로 POST라서 PUT으로 변경
					return httprequest;
				}))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 수정 Error 존재하지 않는 리뷰")
	void t10() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(1L)
			.title("Updated Review Title")
			.content("This is an updated review content.")
			.spoilerStatus(SpoilerStatus.TRUE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/reviews/put/2312424124212132")
				.file(reviewRequestPart) // JSON 데이터 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(httprequest -> {
					httprequest.setMethod("PUT"); // multipart()는 기본적으로 POST라서 PUT으로 변경
					return httprequest;
				}))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-001"))
			.andExpect(jsonPath("$.message").value("리뷰를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("리뷰 수정 Error 존재하지 않는 웹툰")
	void t11() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(132142412421L)
			.title("Updated Review Title")
			.content("This is an updated review content.")
			.spoilerStatus(SpoilerStatus.TRUE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/reviews/put/2")
				.file(reviewRequestPart) // JSON 데이터 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(httprequest -> {
					httprequest.setMethod("PUT"); // multipart()는 기본적으로 POST라서 PUT으로 변경
					return httprequest;
				}))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("WEBTOON-001"))
			.andExpect(jsonPath("$.message").value("웹툰을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("리뷰 수정 Error 권한 없음")
	void t12() throws Exception {
		// JSON 데이터로 변환된 ReviewRequest
		ReviewRequest request = ReviewRequest.builder()
			.webtoonId(1L)
			.title("Updated Review Title")
			.content("This is an updated review content.")
			.spoilerStatus(SpoilerStatus.TRUE)
			.build();

		MockMultipartFile reviewRequestPart = new MockMultipartFile(
			"reviewRequest",
			"",
			"application/json",
			objectMapper.writeValueAsBytes(request)
		);

		mockMvc.perform(multipart("/reviews/put/2")
				.file(reviewRequestPart) // JSON 데이터 추가
				.header(HttpHeaders.AUTHORIZATION, jwtToken2)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(httprequest -> {
					httprequest.setMethod("PUT"); // multipart()는 기본적으로 POST라서 PUT으로 변경
					return httprequest;
				}))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-002"))
			.andExpect(jsonPath("$.message").value("리뷰에 대한 삭제/수정 권한이 없습니다."));
	}


	@Test
	@DisplayName("자신이 작성한 리뷰 조회")
	void t13() throws Exception {
		mockMvc.perform(get("/reviews/me")
				.header(HttpHeaders.AUTHORIZATION, jwtToken))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("조회수 순 리뷰 조회")
	void t14() throws Exception {
		mockMvc.perform(get("/reviews/view-count-desc"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("접속한 유저가 작성한 리뷰 개수 조회")
	void t15() throws Exception {
		mockMvc.perform(get("/reviews/me/count")
				.header(HttpHeaders.AUTHORIZATION, jwtToken))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 제목명 검색")
	void t16() throws Exception {
		mockMvc.perform(get("/reviews/search").param("title", "User 1 -"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("웹툰 ID에 해당하는 리뷰 조회")
	void t17() throws Exception {
		mockMvc.perform(get("/reviews/webtoon/1"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 ID에 해당하는 리뷰 스포일러 처리")
	void t18() throws Exception {
		mockMvc.perform(patch("/reviews/spoiler/2"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리뷰 ID에 해당하는 리뷰 스포일러 처리 Error 존재하지않는 리뷰 ID")
	void t19() throws Exception {
		mockMvc.perform(patch("/reviews/spoiler/2123213214241224"))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value("REVIEW-001"))
			.andExpect(jsonPath("$.message").value("리뷰를 찾을 수 없습니다."));
	}
}
