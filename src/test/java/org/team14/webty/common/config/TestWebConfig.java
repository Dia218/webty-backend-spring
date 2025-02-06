package org.team14.webty.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.team14.webty")
public class TestWebConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
