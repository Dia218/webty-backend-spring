package org.team14.webty.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class WebtyUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long webtyUserId;

	@NotNull
	@Column(unique = true)
	private String nickname;

	@NotNull
	private String profileImg;

	@OneToOne(mappedBy = "webtyUser", fetch = FetchType.LAZY)
	private SocialProvider socialProvider;
}
