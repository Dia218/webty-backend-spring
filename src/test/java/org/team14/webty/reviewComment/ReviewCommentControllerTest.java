package org.team14.webty.reviewComment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.team14.webty.common.cookies.CookieManager;
import org.team14.webty.common.enums.TokenType;
import org.team14.webty.reviewComment.controller.ReviewCommentController;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.service.ReviewCommentService;
import org.team14.webty.security.authentication.CustomAuthenticationFilter;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.security.authentication.WebtyUserDetailsService;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Import;
import org.team14.webty.security.config.TestSecurityConfig;

@WebMvcTest(ReviewCommentController.class)
@Import(TestSecurityConfig.class)
public class ReviewCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ReviewCommentService commentService;
    
    @MockBean
    private CookieManager cookieManager;
    
    @MockBean
    private JwtManager jwtManager;
    
    @MockBean
    private CustomAuthenticationFilter customAuthenticationFilter;

    @MockBean
    private WebtyUserDetailsService userDetailsService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private WebtyUser testUser;
    private CommentResponse testComment;
    private UserDataResponse userDataResponse;
    private WebtyUserDetails userDetails;
    private Authentication authentication;
    private String testToken = "test-token";
    private CommentRequest testRequest;

    @BeforeEach
    void beforeEach() {
        testUser = WebtyUser.builder()
            .userId(1L)
            .nickname("테스트유저")
            .profileImage("test-profile-image")
            .socialProvider(SocialProvider.builder()
                .provider(SocialProviderType.KAKAO)
                .providerId("313213231")
                .build())
            .build();

        userDataResponse = new UserDataResponse(testUser);
        userDetails = new WebtyUserDetails(testUser);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());

        testComment = CommentResponse.builder()
            .commentId(1L)
            .content("테스트 댓글")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .parentId(0L)
            .mentions(new ArrayList<>())
            .childComments(new ArrayList<>())
            .user(userDataResponse)
            .build();

        testRequest = new CommentRequest();
        testRequest.setContent("테스트 댓글");
        testRequest.setParentCommentId(0L);
        testRequest.setMentions(new ArrayList<>());
            
        // JWT 토큰 검증 모킹
        when(jwtManager.validate(testToken)).thenReturn(true);
        when(jwtManager.getAuthentication(testToken)).thenReturn(authentication);
        when(jwtManager.getUserIdByToken(testToken)).thenReturn(testUser.getUserId());
        when(userDetailsService.loadUserByUserId(testUser.getUserId())).thenReturn(userDetails);
        when(cookieManager.getCookieByTokenType(TokenType.ACCESS_TOKEN)).thenReturn(testToken);
        when(cookieManager.getCookieByTokenType(TokenType.REFRESH_TOKEN)).thenReturn(testToken);
        when(jwtManager.recreateTokens(testToken)).thenReturn(new String[]{testToken, testToken});

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void t1() throws Exception {
        when(commentService.createComment(any(WebtyUserDetails.class), any(Long.class), any(CommentRequest.class)))
            .thenReturn(testComment);

        mockMvc.perform(post("/reviews/1/comments/create")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.commentId").value(testComment.getCommentId()))
            .andExpect(jsonPath("$.content").value(testComment.getContent()))
            .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.parentId").value(testComment.getParentId()))
            .andExpect(jsonPath("$.mentions").isArray())
            .andExpect(jsonPath("$.childComments").isArray());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void t2() throws Exception {
        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setContent("수정된 테스트 댓글");
        updateRequest.setParentCommentId(0L);
        updateRequest.setMentions(new ArrayList<>());

        CommentResponse updatedComment = CommentResponse.builder()
            .commentId(1L)
            .content("수정된 테스트 댓글")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .parentId(0L)
            .mentions(new ArrayList<>())
            .childComments(new ArrayList<>())
            .user(userDataResponse)
            .build();

        when(commentService.updateComment(any(Long.class), any(WebtyUserDetails.class), any(CommentRequest.class)))
            .thenReturn(updatedComment);

        mockMvc.perform(put("/reviews/1/comments/1")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.commentId").value(updatedComment.getCommentId()))
            .andExpect(jsonPath("$.content").value(updatedComment.getContent()))
            .andExpect(jsonPath("$.user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.parentId").value(updatedComment.getParentId()))
            .andExpect(jsonPath("$.mentions").isArray())
            .andExpect(jsonPath("$.childComments").isArray());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void t3() throws Exception {
        mockMvc.perform(delete("/reviews/1/comments/1")
                .header("Authorization", "Bearer " + testToken))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    void t4() throws Exception {
        List<CommentResponse> comments = List.of(testComment);
        Page<CommentResponse> commentPage = new PageImpl<>(comments);

        when(commentService.getCommentsByReviewId(any(Long.class), any(Integer.class), any(Integer.class)))
            .thenReturn(commentPage);

        mockMvc.perform(get("/reviews/1/comments")
                .header("Authorization", "Bearer " + testToken)
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].commentId").value(testComment.getCommentId()))
            .andExpect(jsonPath("$.content[0].content").value(testComment.getContent()))
            .andExpect(jsonPath("$.content[0].user.nickname").value(testUser.getNickname()))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.totalPages").exists())
            .andExpect(jsonPath("$.totalElements").exists())
            .andExpect(jsonPath("$.hasNext").exists())
            .andExpect(jsonPath("$.hasPrevious").exists())
            .andExpect(jsonPath("$.isLast").exists());
    }
}
