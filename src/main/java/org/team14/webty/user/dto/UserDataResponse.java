package org.team14.webty.user.dto;

import org.team14.webty.user.entity.WebtyUser;

import lombok.Getter;

@Getter
public class UserDataResponse {
	private final String nickname;
	private final String profileImage;

	public UserDataResponse(WebtyUser webtyUser) {
		this.nickname = webtyUser.getNickname();
		this.profileImage = webtyUser.getProfileImage();
	}
}
