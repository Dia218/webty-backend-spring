package org.team14.webty.user.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.team14.webty.common.util.FileStorageUtil;
import org.team14.webty.user.entity.ProviderType;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.SocialProviderRepository;
import org.team14.webty.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final String DEFAULT_PROFILE_IMAGE_PATH =
		System.getProperty("user.dir") + "/src/main/resources/image/iconmonstr-user-circle-thin-240.png";

	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final SocialProviderRepository socialProviderRepository;
	private final FileStorageUtil fileStorageUtil;

	@Transactional(readOnly = true)
	public WebtyUser findUserByNickname(String nickname) {
		Optional<WebtyUser> opWebtyUser = userRepository.findByNickname(nickname);
		if (opWebtyUser.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 닉네임");
		}
		return opWebtyUser.get();
	}

	// @Transactional(readOnly = true)
	// public WebtyUser findUserById(Long userId) {
	// 	Optional<WebtyUser> opWebtyUser = userRepository.findById(userId);
	// 	if (opWebtyUser.isEmpty()) {
	// 		throw new IllegalArgumentException("존재하지 않는 userId");
	// 	}
	// 	return opWebtyUser.get();
	// }

	@Transactional(readOnly = true)
	public Authentication getAuthentication(String userId) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(userId);  // userId로 사용자 정보 가져오기
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());  // 인증 객체 생성
	}

	@Transactional
	public WebtyUser add(ProviderType providerType, String providerId) {
		SocialProvider socialProvider = SocialProvider.builder()
			.provider(providerType)
			.providerId(providerId)
			.build();
		socialProviderRepository.save(socialProvider);
		socialProviderRepository.flush();

		String nickname = "웹티사랑꾼 %s호".formatted(socialProvider.getSocialId());
		WebtyUser webtyUser = WebtyUser.builder()
			.nickname(nickname)
			.profileImage(DEFAULT_PROFILE_IMAGE_PATH)
			.socialProvider(socialProvider)
			.build();

		userRepository.save(webtyUser);
		return webtyUser;
	}

	@Transactional
	public void modifyNickname(WebtyUser webtyUser, String nickname) {
		webtyUser.modifyNickname(nickname);
		userRepository.save(webtyUser);
	}

	@Transactional
	public void modifyImage(WebtyUser webtyUser, MultipartFile file) throws IOException {
		String filePath = fileStorageUtil.storeImageFile(file, "User_" + webtyUser.getUserId());
		webtyUser.updateProfile(webtyUser.getNickname(), filePath);
		userRepository.save(webtyUser);
	}
}
