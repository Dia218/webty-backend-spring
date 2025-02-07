package org.team14.webty.recommend.enumerate;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeType {
	LIKE("like"),
	HATE("hate");

	private final String type;

	public static LikeType fromString(String value) {
		return Arrays.stream(values())
			.filter(status -> status.type.equalsIgnoreCase(value))
			.findFirst().orElseThrow(() -> new IllegalArgumentException(
				"No enum constant " + LikeType.class.getCanonicalName() + "." + value));
	}
}
