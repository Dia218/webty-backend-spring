package org.team14.webty.webtoon.entity;

import java.util.ArrayList;
import java.util.List;

import org.team14.webty.webtoon.enumerate.Platform;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long webtoonId;

	private String webtoonName;

	@Enumerated(EnumType.STRING)
	private Platform platform;

	private String webtoonLink;

	private String thumbnailUrl; // 수정이 필요할 수도 있음

	private String authors;

	private boolean finished;

	@OneToMany(mappedBy = "webtoon", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Favorite> favorites = new ArrayList<>();

}
