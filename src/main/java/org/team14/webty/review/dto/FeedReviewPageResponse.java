package org.team14.webty.review.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedReviewPageResponse {
	private List<FeedReviewResponse> content;
	private int totalPages; // 전체 페이지 수
	private long totalElements; // 전체 리뷰 수
	private int numberOfElements; // 현재 페이지의 리뷰 수
	private int size; // 페이지당 리뷰 수
	private int number; // 현재 페이지 번호 (0부터 시작)
	private boolean first; // 현재 페이지가 첫 번째 페이지인지 여부
	private boolean last; // 현재 페이지가 마지막 페이지인지 여부
	private boolean empty; // 리뷰 목록이 비어있는지 여부

	public static FeedReviewPageResponse from(Page<FeedReviewResponse> page) {
		return FeedReviewPageResponse.builder()
			.content(page.getContent())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.numberOfElements(page.getNumberOfElements())
			.size(page.getSize())
			.number(page.getNumber())
			.first(page.isFirst())
			.last(page.isLast())
			.empty(page.isEmpty())
			.build();
	}
}
