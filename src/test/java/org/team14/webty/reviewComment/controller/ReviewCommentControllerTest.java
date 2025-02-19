package org.team14.webty.reviewComment.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.enumrate.SpoilerStatus;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
@Transactional
public class ReviewCommentControllerTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private WebtoonRepository webtoonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private ObjectMapper objectMapper;
    private WebtyUser testUser;
    private CommentRequest testRequest;
    private Review testReview;

    @BeforeEach
    void beforeEach() {
        deleteAll();

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
			.build());


        testRequest = new CommentRequest();
        testRequest.setContent("테스트 댓글");
        testRequest.setMentions(new ArrayList<>());

    }

    void deleteAll() {
        reviewCommentRepository.deleteAll();
        reviewRepository.deleteAll();
        webtoonRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void t1() throws Exception {
        Long reviewId = testReview.getReviewId();
        mockMvc.perform(post(getReviewCommentBasicPath(reviewId))
                .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.content").value("테스트 댓글"))
            .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.parentId").isEmpty())
            .andExpect(jsonPath("$.mentions").isArray())
            .andExpect(jsonPath("$.childComments").isArray())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.modifiedAt").exists());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void t2() throws Exception {
        Long testRootCommentId = newRootReviewComment(1).getCommentId();
        newChildReviewComment(1, testRootCommentId);
        newChildReviewComment(2, testRootCommentId);

        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setContent("수정된 테스트 댓글");

        mockMvc.perform(put(getReviewCommentBasicPath(testReview.getReviewId()) + "/" + testRootCommentId)
                .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.commentId").value(testRootCommentId))
            .andExpect(jsonPath("$.content").value("수정된 테스트 댓글"))
            .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.parentId").isEmpty())
            .andExpect(jsonPath("$.mentions").isEmpty())
            .andExpect(jsonPath("$.childComments").isArray())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.modifiedAt").exists());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void t3() throws Exception {
        Long testRootCommentId = newRootReviewComment(1).getCommentId();
        newChildReviewComment(1, testRootCommentId);
        newChildReviewComment(2, testRootCommentId);

        mockMvc.perform(delete(getReviewCommentBasicPath(testReview.getReviewId()) + "/" + testRootCommentId)
                .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId())))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    void t4() throws Exception {
        ReviewComment testRootComment = newRootReviewComment(1);
        Long testRootCommentId = testRootComment.getCommentId();

        ReviewComment testChildComment1 = newChildReviewComment(1, testRootCommentId);
        ReviewComment testChildComment2 = newChildReviewComment(2, testRootCommentId);

        mockMvc.perform(get(getReviewCommentBasicPath(testReview.getReviewId()))
                .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].commentId").value(testRootCommentId))
            .andExpect(jsonPath("$.content[0].content").value(testRootComment.getContent()))
            .andExpect(jsonPath("$.content[0].user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.content[0].childComments[0].commentId").value(testChildComment2.getCommentId()))
            .andExpect(jsonPath("$.content[0].childComments[0].content").value(testChildComment2.getContent()))
            .andExpect(jsonPath("$.content[0].childComments[0].parentId").value(testChildComment2.getParentId()))
            .andExpect(jsonPath("$.content[0].childComments[1].commentId").value(testChildComment1.getCommentId()))
            .andExpect(jsonPath("$.content[0].childComments[1].content").value(testChildComment1.getContent()))
            .andExpect(jsonPath("$.content[0].childComments[1].parentId").value(testChildComment1.getParentId()))
            .andExpect(jsonPath("$.content[0].childComments[0].user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.totalPages").isNumber())
            .andExpect(jsonPath("$.totalElements").isNumber())
            .andExpect(jsonPath("$.hasNext").isBoolean())
            .andExpect(jsonPath("$.hasPrevious").isBoolean())
            .andExpect(jsonPath("$.isLast").isBoolean())
            .andExpect(jsonPath("$.content[0].createdAt").exists())
            .andExpect(jsonPath("$.content[0].modifiedAt").exists())
            .andExpect(jsonPath("$.content[0].childComments[0].createdAt").exists())
            .andExpect(jsonPath("$.content[0].childComments[0].modifiedAt").exists());
    }

	private ReviewComment newRootReviewComment(int number) {
		return reviewCommentRepository.save(
			ReviewComment.builder()
				.user(testUser)
				.review(testReview)
				.content("테스트 댓글: " + number)
				.parentId(null)
				.depth(0)
				.mentions(null)
				.build()
		);
	}

	private ReviewComment newChildReviewComment(int number, Long testRootCommentId) {
		return reviewCommentRepository.save(
			ReviewComment.builder()
				.user(testUser)
				.review(testReview)
				.content("테스트 댓글: " + number)
				.parentId(testRootCommentId)
				.depth(1)
				.mentions(null)
				.build()
		);
	}


    private String getReviewCommentBasicPath(Long reviewId) {
        String commentPath = "/reviews/{reviewId}/comments";
        return UriComponentsBuilder.fromPath(commentPath)
            .buildAndExpand(reviewId)
            .toUriString();
    }
}
