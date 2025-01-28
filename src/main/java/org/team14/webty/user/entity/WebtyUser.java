package org.team14.webty.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "users")
public class WebtyUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long userId;

	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;

	@Column(name = "profile_image")
	private String profileImage;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SocialProvider socialProvider;

	public void modifyNickname(String nickname) {
		this.nickname = nickname;
	}

	// 프로필 업데이트를 위한 비즈니스 메서드
	public void updateProfile(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
	}

	// 연관관계 편의 메서드
	public void setSocialProvider(SocialProvider socialProvider) {
		this.socialProvider = socialProvider;
		socialProvider.setUser(this);  // 양방향 관계 설정
	}
}
