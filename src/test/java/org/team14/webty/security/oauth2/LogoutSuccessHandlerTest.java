package org.team14.webty.security.oauth2;

import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.team14.webty.common.cookies.CookieManager;
import org.team14.webty.common.enums.TokenType;
import org.team14.webty.security.token.JwtManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class LogoutSuccessHandlerTest {

	@Mock
	private CookieManager cookieManager;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private JwtManager jwtManager;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private LogoutSuccessHandler logoutSuccessHandler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Authentication authentication;

	@BeforeEach
	void setUp() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void shouldHandleLogoutSuccessfully() {
		/*
		 * 로그아웃을 호출했을 때 정상적으로 작동하는 지 테스트
		 * 1. 쿠키 삭제 확인
		 * 2. `redis`에 로그아웃 정보를 저장했는지 확인
		 * */

		// Given
		String refreshToken = "mockRefreshToken";
		long expirationTime = 1000L;
		when(cookieManager.getCookieByTokenType(TokenType.REFRESH_TOKEN)).thenReturn(refreshToken);
		when(jwtManager.getExpirationTime(refreshToken)).thenReturn(expirationTime);

		// When
		logoutSuccessHandler.logout(request, response, authentication);

		// Then
		verify(cookieManager, times(1)).removeCookie(TokenType.ACCESS_TOKEN);
		verify(cookieManager, times(1)).removeCookie(TokenType.REFRESH_TOKEN);
		verify(cookieManager, times(1)).removeCookie(TokenType.JSESSIONID);
		verify(redisTemplate.opsForValue(), times(1))
			.set(refreshToken, "logout", expirationTime, TimeUnit.MILLISECONDS);
	}
}
