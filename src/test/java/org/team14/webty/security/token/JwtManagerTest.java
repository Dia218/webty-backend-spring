package org.team14.webty.security.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.security.authentication.WebtyUserDetailsService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class JwtManagerTest {

	private final String userId = "testUser";
	@Mock
	private WebtyUserDetailsService webtyUserDetailsService;
	@InjectMocks
	private JwtManager jwtManager;
	private SecretKey secretKey;

	@BeforeEach
	void setUp() {
		String secret = "mysecretkeymysecretkeymysecretkeymysecretkey";
		secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		jwtManager.init();
	}

	@Test
	void createAccessToken_ShouldReturnValidToken() {
		String token = jwtManager.createAccessToken(userId);
		assertNotNull(token);
	}

	@Test
	void createRefreshToken_ShouldReturnValidToken() {
		String token = jwtManager.createRefreshToken(userId);
		assertNotNull(token);
	}

	@Test
	void validate_ValidToken_ShouldReturnTrue() {
		String token = jwtManager.createAccessToken(userId);
		assertTrue(jwtManager.validate(token));
	}

	@Test
	void validate_ExpiredToken_ShouldReturnFalse() {
		String expiredToken = Jwts.builder()
			.claim("userId", userId)
			.issuedAt(new Date(System.currentTimeMillis() - 10000))
			.expiration(new Date(System.currentTimeMillis() - 5000))
			.signWith(secretKey)
			.compact();

		assertFalse(jwtManager.validate(expiredToken));
	}

	@Test
	void getUserId_ShouldReturnCorrectUserId() {
		String token = jwtManager.createAccessToken(userId);
		assertEquals(userId, jwtManager.getUserId(token));
	}

	@Test
	void getAuthentication_ShouldReturnAuthenticationObject() {
		WebtyUserDetails userDetails = mock(WebtyUserDetails.class);
		when(webtyUserDetailsService.loadUserByUsername(userId)).thenReturn(userDetails);
		when(userDetails.getAuthorities()).thenReturn(null);

		String token = jwtManager.createAccessToken(userId);
		Authentication authentication = jwtManager.getAuthentication(token);

		assertNotNull(authentication);
		assertEquals(userDetails, authentication.getPrincipal());
	}
}
