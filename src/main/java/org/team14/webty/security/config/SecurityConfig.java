package org.team14.webty.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.team14.webty.security.authentication.CustomAuthenticationFilter;
import org.team14.webty.security.oauth2.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final CustomAuthenticationFilter customAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers(HttpMethod.GET, "/webtoons/{id:\\d+}").permitAll()
					.requestMatchers(HttpMethod.GET, "/webtoons").permitAll()
					.requestMatchers("/logout/kakao", "/user-profile", "/user/**",
						"/favorite/**") // 로그인 해야 접속 가능한 페이지 목록
					.authenticated()
					.anyRequest() // 나머지
					.authenticated())
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.headers(headers ->
				headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.csrf(AbstractHttpConfigurer::disable)
			.oauth2Login(oauth2Login ->
				oauth2Login.successHandler(loginSuccessHandler));

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() { // 스프링 시큐리티를 무시할 페이지 목록 ( = 로그인이 필요없는 페이지 목록)
		return web -> web.ignoring().requestMatchers(
			"h2-console/**", "/error", "/webtoon",
			"/favorite" // 테스트 이후 제거할 목록
		);
	}
}