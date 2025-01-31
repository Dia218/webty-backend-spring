package org.team14.webty.user.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.team14.webty.user.dto.ImageResponse;
import org.team14.webty.user.dto.NicknameRequest;
import org.team14.webty.user.dto.NicknameResponse;
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
	public ResponseEntity<NicknameResponse> changeNickname(@RequestBody @Valid NicknameRequest request) {
		// TODO 인증 받아와서 해야됨
		// 지금은 사용자 임의로 만들어서 진행
		WebtyUser webtyUser = userService.add("변경전", "임시아이디");
		String nickname = request.getNickname();
		userService.modifyNickname(webtyUser, nickname);
		return ResponseEntity.ok(new NicknameResponse("닉네임이 변경되었습니다."));
	}

	// 외래키 연결 확인 & 순환참조하는지 확인용 (지워도 됨)
	@GetMapping("/{nickname}")
	public WebtyUser getUser(@PathVariable String nickname) {
		return userService.findUserByNickname(nickname);
	}

	@PatchMapping("/profileImage")
	public ResponseEntity<ImageResponse> changeProfileImg(@RequestParam("file") MultipartFile file) throws IOException {
		// TODO 인증 받아와서 해야됨
		// 지금은 사용자 임의로 만들어서 진행
		WebtyUser webtyUser = userService.add("변경전", "임시아이디");
		userService.modifyImage(webtyUser, file);
		return ResponseEntity.ok(new ImageResponse("프로필사진이 변경되었습니다."));
	}

	@DeleteMapping("/users/{nickname}")
	public ResponseEntity<String> deleteUser(@PathVariable String nickname) {
		WebtyUser user = userService.findUserByNickname(nickname);

		userService.deleteUser(user);
		return ResponseEntity.ok("계정이 삭제되었습니다.");
		// TODO :response클래스 생성?-계정삭제response
	}

}
