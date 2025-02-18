package org.team14.webty.user.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.team14.webty.security.token.JwtManager;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.profiles.active=test"})
public class UserControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtManager jwtManager;
	private WebtyUser testUser;

	@BeforeEach
	void beforeEach() {
		// 기존 사용자 데이터 삭제
		userRepository.deleteAll();
		// 테스트용 사용자 생성 (필요한 필드에 맞게 설정)
		testUser = userRepository.save(
			WebtyUser.builder()
				.nickname("테스트유저")
				.profileImage("testUserProfileImg")
				.build()
		);
	}

	@Test
	@DisplayName("닉네임 변경 테스트")
	void changeNickname_test() throws Exception {
		// 요청 본문: 변경할 닉네임 정보
		Map<String, String> reqBody = new HashMap<>();
		reqBody.put("nickname", "새닉네임");
		String jsonRequest = objectMapper.writeValueAsString(reqBody);

		mockMvc.perform(patch("/user/nickname")
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.content(jsonRequest)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is("닉네임이 변경되었습니다.")));
	}

	@Test
	@DisplayName("사용자 정보 조회 테스트")
	void getUserData_test() throws Exception {
		mockMvc.perform(get("/user/info")
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("프로필 이미지 변경 테스트")
	void changeProfileImage_test() throws Exception {
		// MockMultipartFile 생성 (file 파라미터 이름은 컨트롤러의 @RequestParam("file")와 일치)
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"profile.jpg",
			"image/jpeg",
			"dummy image content".getBytes()
		);

		// multipart()로 요청을 생성한 뒤, PATCH 메서드로 변경
		mockMvc.perform(multipart("/user/profileImage")
				.file(file)
				.with(request -> {
					request.setMethod("PATCH");
					return request;
				})
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is("프로필사진이 변경되었습니다.")));
	}

	@Test
	@DisplayName("사용자 삭제 테스트")
	void deleteUser_test() throws Exception {
		mockMvc.perform(delete("/user/users")
				.header("Authorization", "Bearer " + jwtManager.createAccessToken(testUser.getUserId()))
				.with(csrf()))
			.andExpect(status().isNoContent());

		// 삭제 후 DB에 사용자 정보가 없는지 확인
		assertFalse(userRepository.findById(testUser.getUserId()).isPresent());
	}
}
