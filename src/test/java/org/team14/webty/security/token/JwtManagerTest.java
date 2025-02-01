package org.team14.webty.security.token;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

@SpringBootTest
class JwtManagerTest {

	private final String nickName = "testUser";

	@Autowired
	UserRepository userRepository;
	@Autowired
	private JwtManager jwtManager;
	private Long userId;
	private String accessToken;
	private String refreshToken;

	@BeforeEach
	void setUp() {
		WebtyUser userUser = new WebtyUser(null, nickName, null, null);
		userId = userRepository.save(userUser).getUserId();
		accessToken = jwtManager.createAccessToken(userId);
		refreshToken = jwtManager.createRefreshToken(userId);
	}

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
		userRepository.flush();
	}

	@Test
	void testCreateAccessToken() {
		assertThat(accessToken).isNotNull();
	}

	@Test
	void testCreateRefreshToken() {
		assertThat(refreshToken).isNotNull();
	}

	@Test
	void testGetExpirationTime() {
		Long expirationTime = jwtManager.getExpirationTime(accessToken);
		assertThat(expirationTime).isGreaterThan(System.currentTimeMillis());
	}

	@Test
	void testValidateToken() {
		assertThat(jwtManager.validate(accessToken)).isTrue();
	}

	@Test
	void testRecreateTokens() {
		String[] newTokens = jwtManager.recreateTokens(refreshToken);
		assertThat(newTokens).hasSize(2);
		assertThat(jwtManager.validate(newTokens[0])).isTrue();
		assertThat(jwtManager.validate(newTokens[1])).isTrue();
	}

	@Test
	void testGetUserId() {
		Long extractedUserId = jwtManager.getUserIdByToken(accessToken);
		assertThat(extractedUserId).isEqualTo(userId);
	}

	@Test
	void testGetAuthentication() {
		Authentication authentication = jwtManager.getAuthentication(accessToken);
		assertThat(authentication).isNotNull();
		assertThat(authentication.getName()).isEqualTo(nickName);
		assertThat(authentication.getAuthorities()).isNotEmpty();
	}

}
