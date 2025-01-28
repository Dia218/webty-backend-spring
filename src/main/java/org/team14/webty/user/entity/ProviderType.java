package org.team14.webty.user.entity;

public enum ProviderType {
	KAKAO("kakao");

	private final String providerName;

	ProviderType(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderName() {
		return providerName;
	}
}
