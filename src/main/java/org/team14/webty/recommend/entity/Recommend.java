package org.team14.webty.recommend.entity;

import org.team14.webty.recommend.enumerate.LikeType;
import org.team14.webty.review.entity.Review;
import org.team14.webty.user.entity.WebtyUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Recommend {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "vote_id")
	private Long voteId;

	@Enumerated(EnumType.STRING)
	private LikeType likeType;

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review reviewId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private WebtyUser userId;
}
