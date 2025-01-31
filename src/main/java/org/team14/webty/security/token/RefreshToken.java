package org.team14.webty.security.token;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refreshToken")
public class RefreshToken {

	@Id
	private String userId; // Redis에서는 보통 userId를 Key로 사용

	private String token;

	@TimeToLive
	private Long expiration; // 만료 시간(초 단위)
}