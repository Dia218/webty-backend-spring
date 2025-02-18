package org.team14.webty.reviewComment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.enumrate.SpoilerStatus;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
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
    private UserDataResponse userDataResponse;
    private CommentRequest testRequest;
    private Review testReview;
    private CommentRequest testRequest2;

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

       userDataResponse = new UserDataResponse(testUser);

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

       testRequest = new CommentRequest();
       testRequest.setContent("테스트 댓글");
       testRequest.setMentions(new ArrayList<>());

    }

    @AfterEach
    void afterEach() {
       reviewCommentRepository.deleteAll();
       reviewRepository.deleteAll();
       webtoonRepository.deleteAll();
       userRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void t1() throws Exception {
       Long reviewId = testReview.getReviewId();
       mockMvc.perform(post("/reviews/" + reviewId + "/comments/create")
             .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(testRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.commentId").value(1L))
          .andExpect(jsonPath("$.content").value("테스트 댓글"))
          .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
          .andExpect(jsonPath("$.parentId").isEmpty())
          .andExpect(jsonPath("$.mentions").isArray())
          .andExpect(jsonPath("$.childComments").isArray());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void t2() throws Exception {
       Long reviewId = testReview.getReviewId();
       String result = mockMvc.perform(post("/reviews/" + reviewId + "/comments/create")
             .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(testRequest)))
          .andReturn().getResponse().getContentAsString();

       JsonNode jsonNode = objectMapper.readTree(result);
       Long commentId = jsonNode.get("commentId").asLong();

       CommentRequest updateRequest = new CommentRequest();
       updateRequest.setContent("수정된 테스트 댓글");

       mockMvc.perform(put("/reviews/" + reviewId + "/comments/" + commentId)
             .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.commentId").value(commentId))
          .andExpect(jsonPath("$.content").value("수정된 테스트 댓글"))
          .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
          .andExpect(jsonPath("$.parentId").isEmpty())
          .andExpect(jsonPath("$.mentions").isArray())
          .andExpect(jsonPath("$.childComments").isArray());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void t3() throws Exception {
       Long reviewId = testReview.getReviewId();
       String result = mockMvc.perform(post("/reviews/" + reviewId + "/comments/create")
          .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testRequest))).andReturn().getResponse().getContentAsString();

       JsonNode jsonNode = objectMapper.readTree(result);
       Long commentId = jsonNode.get("commentId").asLong();

       mockMvc.perform(delete("/reviews/1/comments/" + commentId)
             .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId())))
          .andDo(print())
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    void t4() throws Exception {
       Long reviewId = testReview.getReviewId();
       String result = mockMvc.perform(post("/reviews/" + reviewId + "/comments/create")
          .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testRequest))).andReturn().getResponse().getContentAsString();

       JsonNode jsonNode = objectMapper.readTree(result);
       Long commentId = jsonNode.get("commentId").asLong();

       testRequest2 = new CommentRequest();
       testRequest2.setContent("테스트 댓글2");
       testRequest2.setParentCommentId(commentId);
       testRequest.setMentions(new ArrayList<>());

       String result2 = mockMvc.perform(post("/reviews/" + reviewId + "/comments/create")
          .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testRequest2))).andReturn().getResponse().getContentAsString();

       JsonNode jsonNode2 = objectMapper.readTree(result2);
       Long commentId2 = jsonNode2.get("commentId").asLong();

       mockMvc.perform(get("/reviews/" + reviewId + "/comments")
             .header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
             .accept(MediaType.APPLICATION_JSON)
             .param("page", "0")
             .param("size", "10"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].commentId").value(commentId))
          .andExpect(jsonPath("$.content[0].content").value("테스트 댓글"))
          .andExpect(jsonPath("$.content[0].user.nickname").value(testUser.getNickname()))
          .andExpect(jsonPath("$.content[0].childComments[0].commentId").value(commentId2))
          .andExpect(jsonPath("$.content[0].childComments[0].content").value("테스트 댓글2"))
          .andExpect(jsonPath("$.content[0].childComments[0].parentId").value(commentId))
          .andExpect(jsonPath("$.content[0].childComments[0].user.nickname").value(testUser.getNickname()))
          .andExpect(jsonPath("$.currentPage").value(0))
          .andExpect(jsonPath("$.totalPages").isNumber())
          .andExpect(jsonPath("$.totalElements").isNumber())
          .andExpect(jsonPath("$.hasNext").isBoolean())
          .andExpect(jsonPath("$.hasPrevious").isBoolean())
          .andExpect(jsonPath("$.isLast").isBoolean());
    }
}