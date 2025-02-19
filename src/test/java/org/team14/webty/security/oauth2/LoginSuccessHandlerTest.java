package org.team14.webty.security.oauth2;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;
import org.team14.webty.common.cookies.CookieManager;
import org.team14.webty.common.enums.TokenType;
import org.team14.webty.security.policy.ExpirationPolicy;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {

	@Mock
	private CookieManager cookieManager;

	@Mock
	private JwtManager jwtManager;

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private LoginSuccessHandler loginSuccessHandler;

	private OAuth2AuthenticationToken authenticationToken;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(loginSuccessHandler, "REDIRECT_URI", "callbackURI");

		// Mock OAuth2User (OAuth2 로그인 사용자 정보)
		HashMap<String, Object> attributes = new HashMap<>();
		attributes.put("id", "123456789"); // OAuth2 Provider의 사용자 ID

		OAuth2User oAuth2User = new DefaultOAuth2User(
			java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes, "id"
		);

		// Mock OAuth2AuthenticationToken
		authenticationToken = new OAuth2AuthenticationToken(
			oAuth2User, oAuth2User.getAuthorities(), "kakao"
		);
	}

	@Test
	void OAuth2_Login() throws IOException {
		// Given
		Long userId = 100L;
		String accessToken = "mockAccessToken";
		String refreshToken = "mockRefreshToken";

		when(userService.existSocialProvider("123456789")).thenReturn(Optional.of(userId)); // 기존 유저 존재
		when(jwtManager.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtManager.createRefreshToken(userId)).thenReturn(refreshToken);

		// When
		loginSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken);

		// Then
		verify(userService).existSocialProvider("123456789"); // 존재하는 유저 확인
		verify(jwtManager).createAccessToken(userId); // 액세스 토큰 생성 확인
		verify(jwtManager).createRefreshToken(userId); // 리프레시 토큰 생성 확인
		verify(cookieManager).setCookie(TokenType.ACCESS_TOKEN, accessToken, ExpirationPolicy.getAccessTokenExpirationTime());
		verify(cookieManager).setCookie(TokenType.REFRESH_TOKEN, refreshToken, ExpirationPolicy.getRefreshTokenExpirationTime());
	}

	@Test
	void OAuth2_Login_Create_New_WebtyUser() throws IOException {
		// Given
		Long newUserId = 200L;
		String accessToken = "mockAccessTokenNew";
		String refreshToken = "mockRefreshTokenNew";

		when(userService.existSocialProvider("123456789")).thenReturn(Optional.empty()); // 신규 유저
		when(userService.createUser(SocialProviderType.KAKAO, "123456789")).thenReturn(newUserId);
		when(jwtManager.createAccessToken(newUserId)).thenReturn(accessToken);
		when(jwtManager.createRefreshToken(newUserId)).thenReturn(refreshToken);

		// When
		loginSuccessHandler.onAuthenticationSuccess(request, response, authenticationToken);

		// Then
		verify(userService).existSocialProvider("123456789");
		verify(userService).createUser(SocialProviderType.KAKAO, "123456789"); // 신규 유저 등록 확인
		verify(jwtManager).createAccessToken(newUserId);
		verify(jwtManager).createRefreshToken(newUserId);
		verify(cookieManager).setCookie(TokenType.ACCESS_TOKEN, accessToken, ExpirationPolicy.getAccessTokenExpirationTime());
		verify(cookieManager).setCookie(TokenType.REFRESH_TOKEN, refreshToken, ExpirationPolicy.getRefreshTokenExpirationTime());
	}
}
