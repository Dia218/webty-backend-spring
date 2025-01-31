package org.team14.webty.security.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.team14.webty.user.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class JwtManagerTest {

	@Value("${jwt.secret}")
	private String secret; // remove final, so it can be injected

	@Autowired
	private JwtManager jwtManager;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private UserService userService;

	private String userId;
	private String accessToken;
	private String refreshToken;

	@BeforeEach
	void setUp() {
		userId = "testUser";
		System.out.println("Secret Key: " + secret); // Add this line to check the value
		when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(
			new RefreshToken(userId, "dummyToken", 3600L));
		jwtManager.init(); // initialization process called
		accessToken = jwtManager.createAccessToken(userId);
		refreshToken = jwtManager.createRefreshToken(userId);
	}

	@Test
	void testCreateAccessToken() {
		assertNotNull(accessToken);
		assertTrue(accessToken.startsWith("eyJ"));
	}
}
