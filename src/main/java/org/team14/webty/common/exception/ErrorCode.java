package org.team14.webty.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "유저를 찾을 수 없습니다."),
	USER_LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED,"USER-002","로그인이 필요합니다."),
	USER_NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST,"USER-003", "닉네임이 중복 되었습니다."),
	TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "TOKEN-001","토큰이 유효하지 않습니다."),
	WEBTOON_NOT_FOUND(HttpStatus.NOT_FOUND, "WEBTOON-001", "웹툰을 찾을 수 없습니다."),
	ALREADY_FAVORITED_WEBTOON(HttpStatus.BAD_REQUEST, "WEBTOON-002", "이미 관심 웹툰으로 등록되었습니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-001", "리뷰를 찾을 수 없습니다."),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-001", "댓글을 찾을 수 없습니다."),
	COMMENT_WRITING_RESTRICTED(HttpStatus.BAD_REQUEST, "COMMENT-002", "더 이상 대댓글을 작성할 수 없습니다."),
	COMMENT_PERMISSION_DENIED(HttpStatus.UNAUTHORIZED, "COMMENT-003", "댓글에 대한 삭제/수정 권한이 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL-001", "서버 내부 오류");


	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}
}
