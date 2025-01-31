package org.team14.webty.user.entity;

public enum ProviderType {
	KAKAO("kakao");

	private final String providerName;

	ProviderType(String providerName) {
		this.providerName = providerName;
	}

	public static ProviderType of(String provider) {
		if (provider == null) {
			return null; // provider 문자열이 null인 경우 처리 (또는 예외 발생)
		}

		// 대소문자 구분 없이 비교하기 위해 provider 문자열을 소문자로 변환
		String lowerCaseProvider = provider.toLowerCase();

		for (ProviderType type : ProviderType.values()) {
			if (type.providerName.equals(lowerCaseProvider)) {
				return type;
			}
		}

		// 일치하는 ProviderType이 없는 경우 처리 (또는 예외 발생)
		return null; // 또는 throw new IllegalArgumentException("Unknown provider: " + provider);
	}

	public String getProviderName() {
		return providerName;
	}
}
