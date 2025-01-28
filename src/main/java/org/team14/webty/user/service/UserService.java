package org.team14.webty.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.user.entity.ProviderType;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.SocialProviderRepository;
import org.team14.webty.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final SocialProviderRepository socialProviderRepository;

	@Transactional
	public void modifyNickname(WebtyUser webtyUser, String nickname) {
		webtyUser.modifyNickname(nickname);
		userRepository.save(webtyUser);
	}

	@Transactional
	public WebtyUser add(String provider, String providerId) {
		SocialProvider socialProvider = SocialProvider.builder()
			.provider(ProviderType.KAKAO)
			.providerId(providerId)
			.build();
		socialProviderRepository.save(socialProvider);
		socialProviderRepository.flush();

		String nickname = "웹티사랑꾼 %s호".formatted(socialProvider.getSocialId());
		String profileImage = "임시 url";
		WebtyUser webtyUser = WebtyUser.builder()
			.nickname(nickname)
			.profileImage(profileImage)
			.build();

		// 연관관계 처리 (webtyUser - socialProvider 쌍방)
		webtyUser.setSocialProvider(socialProvider);

		userRepository.save(webtyUser);
		return webtyUser;
	}
}
