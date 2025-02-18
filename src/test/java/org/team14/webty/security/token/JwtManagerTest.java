package org.team14.webty.security.token;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@ActiveProfiles("test")  // application-test.yml을 사용하기 위해 추가
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtManagerTest {

	private final String nickName = "testUser";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtManager jwtManager;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;  // 실제 Redis 활용

	private Long userId;
	private String accessToken;
	private String refreshToken;

	@BeforeEach
	void setUp() {
		// 1. 테스트 유저 생성 및 저장
		WebtyUser testUser = new WebtyUser(null, nickName, null, null);
		userId = userRepository.save(testUser).getUserId();

		// 2. JWT 토큰 생성
		accessToken = jwtManager.createAccessToken(userId);
		refreshToken = jwtManager.createRefreshToken(userId);
	}

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
		redisTemplate.delete(refreshToken);  // Redis에서 refreshToken 삭제
	}

	@Test
	void testCreateAccessToken() {
		assertThat(accessToken).isNotNull();
		assertThat(jwtManager.validate(accessToken)).isTrue();
	}

	@Test
	void testCreateRefreshToken() {
		assertThat(refreshToken).isNotNull();
		assertThat(jwtManager.validate(refreshToken)).isTrue();

		// Redis에 refreshToken이 저장되었는지 검증
		String storedUserId = redisTemplate.opsForValue().get(refreshToken);
		assertThat(storedUserId).isEqualTo(String.valueOf(userId));
	}

	@Test
	void testGetExpirationTime() {
		Long expirationTime = jwtManager.getExpirationTime(accessToken);
		assertThat(expirationTime).isGreaterThan(System.currentTimeMillis());
	}

	@Test
	void testValidateToken_active() {
		assertThat(jwtManager.validate(accessToken)).isTrue();
	}

	@Test
	void testValidateToken_expired() {
		// 만료된 토큰 생성 (0ms 후 만료)
		String expiredToken = Jwts.builder()
			.claim("userId", userId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() - 1000)) // 이미 만료됨
			.signWith(Keys.hmacShaKeyFor("testtesttesttesttesttesttesttesttesttest".getBytes()))
			.compact();

		assertThat(jwtManager.validate(expiredToken)).isFalse();
	}

	@Test
	void testRecreateTokens() {
		String[] newTokens = jwtManager.recreateTokens(refreshToken);
		assertThat(newTokens).hasSize(2);
		assertThat(jwtManager.validate(newTokens[0])).isTrue();
		assertThat(jwtManager.validate(newTokens[1])).isTrue();

		// Redis에 새로운 refreshToken이 저장되었는지 확인
		String storedUserId = redisTemplate.opsForValue().get(newTokens[1]);
		assertThat(storedUserId).isEqualTo(String.valueOf(userId));
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
