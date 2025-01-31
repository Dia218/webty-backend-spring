package org.team14.webty.security.token;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.security.authentication.WebtyUserDetailsService;
import org.team14.webty.security.policy.ExpirationPolicy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtManager {
	private final RefreshTokenRepository refreshTokenRepository;
	private final WebtyUserDetailsService webtyUserDetailsService;

	@Value("${jwt.secret}")
	private String secret;
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		log.info("JWT 보안 키가 성공적으로 생성되었습니다.");
	}

	public String createAccessToken(String userId) {
		return Jwts.builder()
			.claim("userId", userId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ExpirationPolicy.getAccessTokenExpirationTime()))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(String userId) {
		String refreshToken = Jwts.builder()
			.claim("userId", userId)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ExpirationPolicy.getRefreshTokenExpirationTime()))
			.signWith(secretKey)
			.compact();

		RefreshToken token = new RefreshToken(userId, refreshToken, ExpirationPolicy.getRefreshTokenExpirationTime());
		refreshTokenRepository.save(token);

		return refreshToken;
	}

	public Long getExpirationTime(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration().getTime();
		} catch (JwtException e) {
			log.error("인증 토큰이 유효하지 않거나 만료되었습니다: {}", e.getMessage());
			throw new RuntimeException("유효하지 않은 인증 토큰입니다", e);
		}
	}

	public boolean validate(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return !isExpired(token);
		} catch (JwtException e) {
			log.error("인증 토큰 검증에 실패했습니다: {}", e.getMessage());
			return false;
		}
	}

	private boolean isExpired(String token) {
		Date expiration = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration();
		return expiration.before(new Date());
	}

	public String[] recreateTokens(String refreshToken) {
		String userId = getUserId(refreshToken);

		refreshTokenRepository.deleteByUserId(userId); // 기존 리프레시 토큰 만료 처리

		String newAccessToken = createAccessToken(userId);
		String newRefreshToken = createRefreshToken(userId);

		return new String[] {newAccessToken, newRefreshToken};
	}

	public String getUserId(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("userId", String.class);
		} catch (JwtException e) {
			log.error("인증 토큰에서 사용자 정보를 가져오는데 실패했습니다: {}", e.getMessage());
			throw new RuntimeException("유효하지 않은 인증 토큰입니다", e);
		}
	}

	public Authentication getAuthentication(String accessToken) {
		String userId = getUserId(accessToken);
		WebtyUserDetails webtyUserDetails = webtyUserDetailsService.loadUserByUsername(userId);  // userId로 사용자 정보 가져오기
		return new UsernamePasswordAuthenticationToken(webtyUserDetails, "",
			webtyUserDetails.getAuthorities());  // 인증 객체 생성
	}
}
