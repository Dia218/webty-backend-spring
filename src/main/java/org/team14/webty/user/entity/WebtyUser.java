package org.team14.webty.user.entity;

import org.springframework.data.annotation.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "users")  // 테이블명을 복수형으로 변경
public class WebtyUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long userId;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "profile_image")
	private String profileImage;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private SocialProvider socialProvider;

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