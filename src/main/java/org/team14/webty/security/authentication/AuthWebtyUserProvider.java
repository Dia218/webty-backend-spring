package org.team14.webty.security.authentication;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import org.springframework.stereotype.Component;
import org.team14.webty.user.entity.WebtyUser;

@Component
public class AuthWebtyUserProvider {
	public WebtyUser getAuthenticatedWebtyUser(WebtyUserDetails webtyUserDetails) {
		if (webtyUserDetails == null || webtyUserDetails.getWebtyUser() == null) {
			try { // 예외 처리 정리해야함
				throw new UserPrincipalNotFoundException("로그인이 필요합니다.");
			} catch (UserPrincipalNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return webtyUserDetails.getWebtyUser();
	}
}
