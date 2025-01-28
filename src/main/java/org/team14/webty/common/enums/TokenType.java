package org.team14.webty.common.enums;

import lombok.Getter;

@Getter
public enum TokenType {
	ACCESS_TOKEN("ACCESS_TOKEN"),
	REFRESH_TOKEN("REFRESH_TOKEN");

	private final String name;

	TokenType(String name) {
		this.name = name;
	}
}
