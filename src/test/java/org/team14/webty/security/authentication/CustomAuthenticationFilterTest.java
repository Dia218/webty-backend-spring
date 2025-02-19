package org.team14.webty.security.authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.team14.webty.common.cookies.CookieManager;
import org.team14.webty.common.enums.TokenType;
import org.team14.webty.security.policy.ExpirationPolicy;
import org.team14.webty.security.token.JwtManager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFilterTest {

	@InjectMocks
	private CustomAuthenticationFilter customAuthenticationFilter;

	@Mock
	private JwtManager jwtManager;

	@Mock
	private CookieManager cookieManager;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private Authentication authentication;

	private final String accessToken = "validAccessToken";
	private final String refreshToken = "validRefreshToken";
	private final String newAccessToken = "newAccessToken";
	private final String newRefreshToken = "newRefreshToken";

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext(); // SecurityContext 초기화
	}

	@Test
	void test_AccessToken_Valid() throws Exception {
		// Given
		when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
		when(jwtManager.validate(accessToken)).thenReturn(true);
		when(jwtManager.getAuthentication(accessToken)).thenReturn(authentication);

		// When
		customAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// Then
		assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void test_AccessToken_Expired_But_RefreshToken_Valid() throws Exception {
		// Given
		when(request.getHeader("Authorization")).thenReturn(null);
		when(cookieManager.getCookieByTokenType(TokenType.ACCESS_TOKEN)).thenReturn(null);
		when(cookieManager.getCookieByTokenType(TokenType.REFRESH_TOKEN)).thenReturn(refreshToken);
		when(jwtManager.validate(refreshToken)).thenReturn(true); // access는 만료, refresh로 검증했을 때

		when(jwtManager.recreateTokens(refreshToken)).thenReturn(new String[]{newAccessToken, newRefreshToken});
		when(jwtManager.validate(newAccessToken)).thenReturn(true);
		when(jwtManager.getAuthentication(newAccessToken)).thenReturn(authentication);

		// When
		customAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// Then
		assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
		verify(cookieManager).setCookie(TokenType.ACCESS_TOKEN, newAccessToken, ExpirationPolicy.getAccessTokenExpirationTime());
		verify(cookieManager).setCookie(TokenType.REFRESH_TOKEN, newRefreshToken, ExpirationPolicy.getRefreshTokenExpirationTime());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void test_AccessToken_Invalid_But_RefreshToken_Valid() throws Exception {
		// Given: Access Token이 존재하지만 잘못된 값 (변조된 값)
		when(request.getHeader("Authorization")).thenReturn("Bearer invalidAccessToken");
		when(jwtManager.validate("invalidAccessToken")).thenReturn(false);

		when(cookieManager.getCookieByTokenType(TokenType.REFRESH_TOKEN)).thenReturn(refreshToken);
		when(jwtManager.validate(refreshToken)).thenReturn(true);
		when(jwtManager.recreateTokens(refreshToken)).thenReturn(new String[]{newAccessToken, newRefreshToken});
		when(jwtManager.validate(newAccessToken)).thenReturn(true);
		when(jwtManager.getAuthentication(newAccessToken)).thenReturn(authentication);

		// When
		customAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// Then
		assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
		verify(cookieManager).setCookie(TokenType.ACCESS_TOKEN, newAccessToken, ExpirationPolicy.getAccessTokenExpirationTime());
		verify(cookieManager).setCookie(TokenType.REFRESH_TOKEN, newRefreshToken, ExpirationPolicy.getRefreshTokenExpirationTime());
		verify(filterChain).doFilter(request, response);
	}


	@Test // 이 테스트가 둘다 변조되거나 둘다 만료되었을 때를 포함함
	void test_AccessToken_And_RefreshToken_Invalid() throws Exception {
		// Given
		when(request.getHeader("Authorization")).thenReturn(null);
		when(cookieManager.getCookieByTokenType(TokenType.ACCESS_TOKEN)).thenReturn(null);
		when(cookieManager.getCookieByTokenType(TokenType.REFRESH_TOKEN)).thenReturn(null);

		// When
		customAuthenticationFilter.doFilterInternal(request, response, filterChain);

		// Then
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(filterChain, never()).doFilter(request, response);
	}
}
