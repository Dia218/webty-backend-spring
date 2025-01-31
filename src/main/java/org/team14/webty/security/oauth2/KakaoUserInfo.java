package org.team14.webty.security.oauth2;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KakaoUserInfo {
	private Map<String, Object> attributes;

	public String getProviderId() {
		return attributes.get("id").toString();
	}

	public String getProvider() {
		return "kakao";
	}

}
