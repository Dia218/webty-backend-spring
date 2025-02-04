package org.team14.webty.reviewComment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.team14.webty.common.config.WebConfig;
import org.team14.webty.reviewComment.controller.ReviewCommentController;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.service.ReviewCommentService;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.dto.UserDataResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collections;

@WebMvcTest(ReviewCommentController.class)
@Import({ReviewCommentControllerTest.TestSecurityConfig.class, ReviewCommentControllerTest.TestWebConfig.class})
class ReviewCommentControllerTest {

	@MockBean
	private ReviewCommentService commentService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private WebtyUser testUser;

	@BeforeEach
	void setUp() {
		testUser = createTestUser();
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()));
	}

	private WebtyUser createTestUser() {
		return WebtyUser.builder()
			.userId(1L)
			.nickname("testUser")
			.build();
	}

	@Test
	@DisplayName("댓글 작성 테스트")
	void createCommentTest() throws Exception {
		// given
		CommentRequest request = new CommentRequest();
		request.setComment("테스트 댓글");

		CommentResponse expectedResponse = CommentResponse.builder()
			.commentId(1L)
			.comment("테스트 댓글")
			.build();

		when(commentService.createComment(any(WebtyUser.class), any(Long.class), any(CommentRequest.class)))
			.thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(post("/api/reviews/{reviewId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.user.nickname").value("testUser"))
			.andExpect(jsonPath("$.data.commentId").value(1L))
			.andExpect(jsonPath("$.data.comment").value("테스트 댓글"));
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateCommentTest() throws Exception {
		// given
		CommentRequest request = new CommentRequest();
		request.setComment("수정된 댓글");

		CommentResponse expectedResponse = CommentResponse.builder()
			.commentId(1L)
			.comment("수정된 댓글")
			.build();

		when(commentService.updateComment(any(Long.class), any(WebtyUser.class), any(CommentRequest.class)))
			.thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(put("/api/reviews/{reviewId}/comments/{commentId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.user.nickname").value("testUser"))
			.andExpect(jsonPath("$.data.commentId").value(1L))
			.andExpect(jsonPath("$.data.comment").value("수정된 댓글"));
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void deleteCommentTest() throws Exception {
		doNothing().when(commentService).deleteComment(any(Long.class), any(WebtyUser.class));

		mockMvc.perform(delete("/api/reviews/{reviewId}/comments/{commentId}", 1L, 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.user.nickname").value("testUser"))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@DisplayName("대댓글 작성 테스트")
	void createReplyTest() throws Exception {
		// given
		CommentRequest request = new CommentRequest();
		request.setComment("대댓글");
		request.setParentCommentId(1L);

		CommentResponse expectedResponse = CommentResponse.builder()
			.commentId(2L)
			.comment("대댓글")
			.parentId(1L)
			.build();

		when(commentService.createComment(any(WebtyUser.class), any(Long.class), any(CommentRequest.class)))
			.thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(post("/api/reviews/{reviewId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.user.nickname").value("testUser"))
			.andExpect(jsonPath("$.data.commentId").value(2L))
			.andExpect(jsonPath("$.data.comment").value("대댓글"))
			.andExpect(jsonPath("$.data.parentId").value(1L));
	}

	@Configuration
	@EnableWebSecurity
	static class TestSecurityConfig {
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
					.anyRequest().permitAll());
			return http.build();
		}
	}

	@Configuration
	@EnableWebMvc
	@ComponentScan(basePackages = "org.team14.webty.reviewComment.controller")
	static class TestWebConfig extends WebConfig {
		@Bean
		public ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}
}
