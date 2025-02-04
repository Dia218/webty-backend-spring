package org.team14.webty.reviewComment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.common.exception.BusinessException;
import org.team14.webty.common.exception.ErrorCode;
import org.team14.webty.review.entity.Review;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.reviewComment.mapper.ReviewCommentMapper;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentService {
	private final ReviewCommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final ReviewCommentMapper commentMapper;

	@Transactional
	@CacheEvict(value = "comments", key = "#reviewId")
	public CommentResponse createComment(WebtyUser user, Long reviewId, CommentRequest request) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		Long parentId = request.getParentCommentId();
		if (parentId != null) {
			// 부모 댓글이 존재하는지 확인
			ReviewComment parentComment = commentRepository.findById(parentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
			if (parentComment.getDepth() >= 2) {
				throw new BusinessException(ErrorCode.COMMENT_WRITING_RESTRICTED);
			}
		}

		ReviewComment comment = commentMapper.toEntity(request, user, review);

		// 멘션된 사용자들 처리
		if (request.getMentionedUsernames() != null && !request.getMentionedUsernames().isEmpty()) {
			Set<WebtyUser> mentionedUsers = request.getMentionedUsernames().stream()
				.map(username -> userRepository.findByNickname(username)
					.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)))
				.collect(Collectors.toSet());
			comment.getMentionedUsers().addAll(mentionedUsers);

			// TODO: 멘션된 사용자들에게 알림 보내기
			notifyMentionedUsers(mentionedUsers, comment);
		}

		ReviewComment savedComment = commentRepository.save(comment);
		return commentMapper.toResponse(savedComment);
	}

	private void notifyMentionedUsers(Set<WebtyUser> mentionedUsers, ReviewComment comment) {
		// 알림 로직 구현
		// 예: 이메일 발송, 푸시 알림 등
	}

	@Transactional
	@CacheEvict(value = "comments", key = "#comment.review.reviewId")
	public CommentResponse updateComment(Long commentId, WebtyUser user, CommentRequest request) {
		ReviewComment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
		}

		comment.updateComment(request.getComment());
		return commentMapper.toResponse(comment);
	}

	@Transactional
	@CacheEvict(value = "comments", key = "#comment.review.reviewId")
	public void deleteComment(Long commentId, WebtyUser user) {
		ReviewComment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
		}

		// 대댓글이 있는 경우의 처리 정책 명확화 필요
		List<ReviewComment> childComments = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
		if (!childComments.isEmpty()) {
			// 정책 1: 대댓글도 모두 삭제
			commentRepository.deleteAll(childComments);
			commentRepository.delete(comment);

			// 정책 2: 소프트 삭제 (내용만 변경)
			// comment.updateComment("삭제된 댓글입니다");
			// comment.markAsDeleted();  // 삭제 표시 필드 추가 필요
		} else {
			commentRepository.delete(comment);
		}
	}

	@Cacheable(value = "comments", key = "#reviewId")
	public List<CommentResponse> getCommentsByReviewId(Long reviewId) {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		Page<ReviewComment> commentPage = commentRepository
			.findAllByReviewIdOrderByDepthAndCreatedAt(reviewId, pageable);
		List<ReviewComment> allComments = commentPage.getContent();  // Page를 List로 변환

		// 댓글들을 Map으로 구성 (parentId를 키로 사용)
		Map<Long, List<CommentResponse>> commentMap = new HashMap<>();
		List<CommentResponse> rootComments = new ArrayList<>();

		// 댓글 트리 구조 구성
		for (ReviewComment comment : allComments) {
			CommentResponse response = commentMapper.toResponse(comment);
			if (comment.getParentId() == null) {
				rootComments.add(response);
			} else {
				commentMap
					.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>())
					.add(response);
			}
		}

		// 각 댓글의 대댓글 설정
		for (CommentResponse response : rootComments) {
			setChildComments(response, commentMap);
		}

		return rootComments;
	}

	private void setChildComments(CommentResponse parent, Map<Long, List<CommentResponse>> commentMap) {
		List<CommentResponse> children = commentMap.getOrDefault(parent.getCommentId(), new ArrayList<>());
		//parent.setChildComments(children); //에러나서 임시 주석처리
		for (CommentResponse child : children) {
			setChildComments(child, commentMap);
		}
	}
}
