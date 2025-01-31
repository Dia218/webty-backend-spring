package org.team14.webty.user.entity;

import java.util.ArrayList;
import java.util.List;

import org.team14.webty.webtoon.entity.Favorite;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "webty_user")
public class WebtyUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long userId;

	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;

	@Column(name = "profile_image")
	private String profileImage;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "social_provider_id")
	private SocialProvider socialProvider;

	@OneToMany(mappedBy = "webtyUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Favorite> favorites = new ArrayList<>();

	public WebtyUser(Long userId, String nickname) {
		this.userId = userId;
		this.nickname = nickname;
	}

	public void modifyNickname(String nickname) {
		this.nickname = nickname;
	}

	// 프로필 업데이트를 위한 비즈니스 메서드
	public void updateProfile(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
	}
}
