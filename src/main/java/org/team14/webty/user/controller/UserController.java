package org.team14.webty.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team14.webty.user.dto.NicknameRequest;
import org.team14.webty.user.dto.NicknameResponse;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PatchMapping("/nickname")
	public ResponseEntity<NicknameResponse> changeNickname(@RequestBody @Valid NicknameRequest request) {
		// TODO 인증 받아와서 해야됨
		// 지금은 사용자 임의로 만들어서 진행
		WebtyUser webtyUser = userService.add("변경전", "이미지URL");
		String nickname = request.getNickname();
		userService.modifyNickname(webtyUser, nickname);
		return ResponseEntity.ok(new NicknameResponse("닉네임이 변경되었습니다."));
	}

}
