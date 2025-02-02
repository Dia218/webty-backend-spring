package org.team14.webty.user.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.team14.webty.common.util.FileStorageUtil;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.SocialProviderRepository;
import org.team14.webty.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final String DEFAULT_PROFILE_IMAGE_PATH =
		System.getProperty("user.dir") + "/src/main/resources/image/iconmonstr-user-circle-thin-240.png";
	private static final String DEFAULT_NICKNAME = "웹티사랑꾼 %s호";

	private final UserRepository userRepository;
	private final SocialProviderRepository socialProviderRepository;
	private final FileStorageUtil fileStorageUtil;

	// @Transactional(readOnly = true)
	// public WebtyUser findUserByNickname(String nickname) {
	// 	Optional<WebtyUser> opWebtyUser = userRepository.findByNickname(nickname);
	// 	if (opWebtyUser.isEmpty()) {
	// 		throw new IllegalArgumentException("존재하지 않는 닉네임");
	// 	}
	// 	return opWebtyUser.get();
	// }

	// @Transactional(readOnly = true)
	// public WebtyUser findUserById(Long userId) {
	// 	Optional<WebtyUser> opWebtyUser = userRepository.findById(userId);
	// 	if (opWebtyUser.isEmpty()) {
	// 		throw new IllegalArgumentException("존재하지 않는 userId");
	// 	}
	// 	return opWebtyUser.get();
	// }

	@Transactional(readOnly = true)
	public Optional<Long> existSocialProvider(String providerId) {
		return socialProviderRepository.findByProviderId(providerId)
			.flatMap(socialProvider -> userRepository.findBySocialProvider(socialProvider)
				.map(WebtyUser::getUserId));
	}

	@Transactional
	public Long createUser(SocialProviderType socialProviderType, String providerId) {
		SocialProvider socialProvider = SocialProvider.builder()
			.provider(socialProviderType)
			.providerId(providerId)
			.build();
		socialProviderRepository.save(socialProvider);
		socialProviderRepository.flush();

		String nickname = DEFAULT_NICKNAME.formatted(socialProvider.getSocialId());
		WebtyUser webtyUser = WebtyUser.builder()
			.nickname(nickname)
			.profileImage(DEFAULT_PROFILE_IMAGE_PATH)
			.socialProvider(socialProvider)
			.build();

		userRepository.save(webtyUser);

		return webtyUser.getUserId();
	}

	public String generateUniqueNickname(SocialProvider socialProvider) {
		String baseNickname = "웹티사랑꾼 %s호".formatted(socialProvider.getSocialId());
		String uniqueNickname = baseNickname;
		int attempt = 1;
		// 닉네임이 만약 중복되었을 경우 값을 추가하는 기능 추가
		while (userRepository.existsByNickname(uniqueNickname)) {
			uniqueNickname = "%s_%d".formatted(baseNickname, attempt);
			attempt++;
		}
		return uniqueNickname;
	}

	@Transactional
	public void modifyNickname(WebtyUser webtyUser, String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		}
		webtyUser.modifyNickname(nickname);
		userRepository.save(webtyUser);
	}

	@Transactional
	public void modifyImage(WebtyUser webtyUser, MultipartFile file) throws IOException {
		String filePath = fileStorageUtil.storeImageFile(file, "User_" + webtyUser.getUserId());
		webtyUser.updateProfile(webtyUser.getNickname(), filePath);
		userRepository.save(webtyUser);
	}

	@Transactional
	public void delete(WebtyUser webtyUser) {

		Optional<WebtyUser> opWebtyUser = userRepository.findById(webtyUser.getUserId());
		if (opWebtyUser.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 유저");
		}
		userRepository.delete(webtyUser);
	}

	@Transactional
	public String findNickNameByUserId(Long userId) {
		WebtyUser webtyUser = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 userId"));
		return webtyUser.getNickname();
	}
}
