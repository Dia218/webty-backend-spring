package org.team14.webty.security.token;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {
	public boolean validate(String token) {

		// 유효한지 검사
		// isExpired 만료 되었는지 확인

		return false;
	}

	private boolean isExpired(String token) {
		return false;
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
