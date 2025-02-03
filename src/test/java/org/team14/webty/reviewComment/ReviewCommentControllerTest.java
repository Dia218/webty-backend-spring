package org.team14.webty.reviewComment;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewCommentControllerTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewCommentRepository commentRepository;

	private WebtyUser testUser;
	private Review testReview;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.build();

		// 테스트 데이터 초기화
		commentRepository.deleteAll();
		reviewRepository.deleteAll();
		userRepository.deleteAll();

		testUser = userRepository.save(WebtyUser.builder()
			.nickname("테스트유저")
			.build());

		testReview = reviewRepository.save(Review.builder()
			.user(testUser)
			.content("테스트 리뷰")
			.build());
	}

	@Test
	@DisplayName("댓글 작성 테스트")
	void createCommentTest() throws Exception {
		CommentRequest request = new CommentRequest();
		request.setComment("테스트 댓글");

		mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser.getNickname()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.comment").value("테스트 댓글"));
	}

	@Test
	@DisplayName("대댓글 작성 테스트")
	void createReplyCommentTest() throws Exception {
		// 먼저 부모 댓글 생성
		CommentRequest parentRequest = new CommentRequest();
		parentRequest.setComment("부모 댓글");

		String parentResult = mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(parentRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long parentId = objectMapper.readTree(parentResult)
			.path("data")
			.path("commentId")
			.asLong();

		// 대댓글 작성
		CommentRequest replyRequest = new CommentRequest();
		replyRequest.setComment("대댓글");
		replyRequest.setParentCommentId(parentId);

		mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(replyRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.parentId").value(parentId));
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateCommentTest() throws Exception {
		// 먼저 댓글 생성
		CommentRequest createRequest = new CommentRequest();
		createRequest.setComment("원본 댓글");

		String result = mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long commentId = objectMapper.readTree(result)
			.path("data")
			.path("commentId")
			.asLong();

		// 댓글 수정
		CommentRequest updateRequest = new CommentRequest();
		updateRequest.setComment("수정된 댓글");

		mockMvc.perform(put("/api/reviews/comments/{commentId}", commentId)
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.comment").value("수정된 댓글"));
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void deleteCommentTest() throws Exception {
		// 먼저 댓글 생성
		CommentRequest request = new CommentRequest();
		request.setComment("삭제될 댓글");

		String result = mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long commentId = objectMapper.readTree(result)
			.path("data")
			.path("commentId")
			.asLong();

		// 댓글 삭제
		mockMvc.perform(delete("/api/reviews/comments/{commentId}", commentId)
				.with(user(testUser)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("대댓글 깊이 제한(depth >= 2) 테스트")
	void createReplyCommentDepthTest() throws Exception {
		// 먼저 부모 댓글 생성
		CommentRequest parentRequest = new CommentRequest();
		parentRequest.setComment("부모 댓글");

		String parentResult = mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(parentRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long parentId = objectMapper.readTree(parentResult)
			.path("data")
			.path("commentId")
			.asLong();

		// 대댓글 작성
		CommentRequest replyRequest = new CommentRequest();
		replyRequest.setComment("대댓글");
		replyRequest.setParentCommentId(parentId);

		mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(replyRequest)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error").value("대댓글은 깊이가 2 이상일 수 없습니다."));
	}

	@Test
	@DisplayName("멘션 기능 테스트")
	void mentionCommentTest() throws Exception {
		CommentRequest request = new CommentRequest();
		request.setComment("테스트 댓글 @테스트유저");

		mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser.getNickname()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.comment").value("테스트 댓글 @테스트유저"));
	}

	@Test
	@DisplayName("권한 없는 사용자의 수정/삭제 시도 테스트")
	void unauthorizedUserTest() throws Exception {
		// 먼저 댓글 생성
		CommentRequest request = new CommentRequest();
		request.setComment("삭제될 댓글");

		String result = mockMvc.perform(post("/api/reviews/{reviewId}/comments", testReview.getReviewId())
				.with(user(testUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long commentId = objectMapper.readTree(result)
			.path("data")
			.path("commentId")
			.asLong();

		// 권한 없는 사용자로 수정 시도
		mockMvc.perform(put("/api/reviews/comments/{commentId}", commentId)
				.with(user("권한 없는 사용자"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new CommentRequest())))
			.andDo(print())
			.andExpect(status().isForbidden());

		// 권한 없는 사용자로 삭제 시도
		mockMvc.perform(delete("/api/reviews/comments/{commentId}", commentId)
				.with(user("권한 없는 사용자")))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("존재하지 않는 댓글 처리 테스트")
	void nonExistingCommentTest() throws Exception {
		// 존재하지 않는 댓글 삭제 시도
		mockMvc.perform(delete("/api/reviews/comments/{commentId}", 999999999)
				.with(user(testUser)))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
}
