package org.team14.webty.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/user/**").permitAll() // 테스트용이라 수정 필요함 permitAll -> authenticated
				.requestMatchers("/favorite/**").permitAll() // 테스트용
				.requestMatchers("/webtoon/**").permitAll() // 테스트용
				.anyRequest().authenticated()
			);
		return http.build();
	}
}