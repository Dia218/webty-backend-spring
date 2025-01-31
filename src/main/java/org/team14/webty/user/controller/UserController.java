package org.team14.webty.user.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.team14.webty.user.dto.ImageResponse;
import org.team14.webty.user.dto.NicknameRequest;
import org.team14.webty.user.dto.NicknameResponse;
import org.team14.webty.user.dto.UserDataResponse;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	@PatchMapping("/nickname")
	public ResponseEntity<NicknameResponse> changeNickname(
		@AuthenticationPrincipal WebtyUser webtyUser,
		@RequestBody @Valid NicknameRequest request) {
		String nickname = request.getNickname();
		userService.modifyNickname(webtyUser, nickname);
		return ResponseEntity.ok(new NicknameResponse("닉네임이 변경되었습니다."));
	}

	// UserInfoResponse가 스프링에 이미 있어서 응답객체의 이름을 UserDataResponse를 사용했습니다.
	@GetMapping("/info")
	public ResponseEntity<UserDataResponse> getUserData(@AuthenticationPrincipal WebtyUser webtyUser) {
		return ResponseEntity.ok(new UserDataResponse(webtyUser));
	}

	@PatchMapping("/profileImage")
	public ResponseEntity<ImageResponse> changeProfileImg(
		@AuthenticationPrincipal WebtyUser webtyUser,
		@RequestParam("file") MultipartFile file) throws IOException {
		userService.modifyImage(webtyUser, file);
		return ResponseEntity.ok(new ImageResponse("프로필사진이 변경되었습니다."));
	}

}
