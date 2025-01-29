package org.team14.webty.security.token;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtManager {

	@Value("${jwt.secret}")
	private String secret;
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public Long getExpirationTime(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration().getTime();
	}

	public boolean validate(String token) {

		// 유효한지 검사
		// isExpired 호출하여 만료 되었는지 확인

		return false;
	}

	private boolean isExpired(String token) {
		return false; //쿠키 만료 여부 구현하기
	}

	public String[] recreateTokens(String refreshToken) {
		//String userId = getUserId(refreshToken);

		//String newAccessToken = createAccessToken(userId);
		//String newRefreshToken = createRefreshToken(userId);

		//return new String[] {newAccessToken, newRefreshToken};
		return null;
	}

	public Authentication getAuthentication(String accessToken) {
		return null;
	}
}
