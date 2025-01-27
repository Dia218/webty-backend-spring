package org.team14.webty.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
			.provider(provider)
			.providerId(providerId)
			.build();
		socialProviderRepository.save(socialProvider);
		socialProviderRepository.flush();

		String nickname = "웹티사랑꾼 %s호".formatted(socialProvider.getSocialId());
		String profileImg = "url";
		WebtyUser webtyUser = WebtyUser.builder()
			.nickname(nickname)
			.profileImg(profileImg)
			.socialProvider(socialProvider)
			.build();

		socialProvider.setWebtyUser(webtyUser);
		
		userRepository.save(webtyUser);
		return webtyUser;
	}
}
