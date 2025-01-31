package org.team14.webty.security.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebtyUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public WebtyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		WebtyUser webtyUser = userRepository.findByNickname(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return new WebtyUserDetails(webtyUser);
	}
}