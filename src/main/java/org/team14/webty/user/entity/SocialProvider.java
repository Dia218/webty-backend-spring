package org.team14.webty.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "social_provider")
public class SocialProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long socialId;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	private ProviderType provider;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private WebtyUser user;

	// 프로바이더 정보 업데이트를 위한 비즈니스 메서드
	public void updateProvider(ProviderType newProvider, String newProviderId) {
		if (this.provider != null) {
			throw new IllegalStateException("프로바이더는 한 번 설정하면 변경할 수 없습니다.");
		}
		this.provider = newProvider;
		this.providerId = newProviderId;
	}

	// 연관관계 편의 메서드
	public void setUser(WebtyUser user) {
		if (this.user != null) {
			throw new IllegalStateException("이미 연결된 사용자가 있습니다.");
		}
		this.user = user;
	}
}
