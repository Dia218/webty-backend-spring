package org.team14.webty.review.entity;

import java.time.LocalDateTime;

import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.webtoon.entity.Webtoon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {
	public SpoilerStatus isSpoiler;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;
	@ManyToOne
	private WebtyUser user;
	private String content;
	private String title;
	@Column(columnDefinition = "integer default 0", nullable = false)
	private Integer viewCount;
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@ManyToOne
	private Webtoon webtoon;

	public void plusViewCount() {
		this.viewCount++;
	}

	public void setSpoilerStatus(SpoilerStatus isSpoiler) {
		this.isSpoiler = isSpoiler;
	}

	public enum SpoilerStatus {
		TRUE, // 스포일러
		FALSE // 비스포일러
	}

	// ... 다른 필드들
}
