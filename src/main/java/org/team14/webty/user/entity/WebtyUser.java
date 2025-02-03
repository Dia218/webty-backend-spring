package org.team14.webty.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "webty_user")
public class WebtyUser implements UserDetails {
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

	public void modifyNickname(String nickname) {
		this.nickname = nickname;
	}

	// 프로필 업데이트를 위한 비즈니스 메서드
	public void updateProfile(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return "";  // 필요한 경우 비밀번호 필드 추가
	}

	@Override
	public String getUsername() {
		return nickname;
	}

	// ... UserDetails의 나머지 메서드 구현
}
