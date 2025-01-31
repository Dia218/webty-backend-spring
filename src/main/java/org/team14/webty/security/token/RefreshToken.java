package org.team14.webty.security.token;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshToken implements Serializable {

	private Long refreshTokenId;
	private Long userId; // WebtyUser userId
	private String token;

	public RefreshToken() {
	}

	public RefreshToken(Long userId, String token) {
		this.userId = userId;
		this.token = token;
	}
}