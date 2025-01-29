package org.team14.webty.security.token;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class RefreshToken implements Serializable {

	private Long refreshTokenId;
	private String userId; // WebtyUser userId
	private String token;

	public RefreshToken() {
	}

	public RefreshToken(String userId, String token) {
		this.userId = userId;
		this.token = token;
	}
}