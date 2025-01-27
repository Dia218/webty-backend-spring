package org.team14.webty.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long socialId;

	@NotNull
	private String provider;

	@NotNull
	private String providerId;

	@OneToOne
	@JoinColumn(name = "webty_user_id")
	private WebtyUser webtyUser;
}
