package org.team14.webty.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetails {
	private final Integer status;
	private final String message;
}
