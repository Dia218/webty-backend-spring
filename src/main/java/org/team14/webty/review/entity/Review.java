package org.team14.webty.review.entity;

import org.team14.webty.user.entity.WebtyUser;

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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId;

	@ManyToOne
	private WebtyUser user;

	private String content;

	@Column(columnDefinition = "integer default 0", nullable = false)
	private Integer viewCount;

	public void plusViewCount() {
		this.viewCount++;
	}

	// ... 다른 필드들
}
