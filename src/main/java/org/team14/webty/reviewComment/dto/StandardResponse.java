package org.team14.webty.reviewComment.dto;

import org.team14.webty.user.dto.UserDataResponse;

import lombok.Getter;

@Getter
public class StandardResponse<T> {
	private final UserDataResponse user;
	private final T data;

	public StandardResponse(UserDataResponse user, T data) {
		this.user = user;
		this.data = data;
	}
} 