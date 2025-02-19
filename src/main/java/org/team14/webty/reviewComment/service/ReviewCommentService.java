package org.team14.webty.reviewComment.service;

import static org.team14.webty.reviewComment.mapper.ReviewCommentMapper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team14.webty.common.exception.BusinessException;
import org.team14.webty.common.exception.ErrorCode;
import org.team14.webty.review.repository.ReviewRepository;
import org.team14.webty.reviewComment.dto.CommentRequest;
import org.team14.webty.reviewComment.dto.CommentResponse;
import org.team14.webty.reviewComment.entity.ReviewComment;
import org.team14.webty.reviewComment.mapper.ReviewCommentMapper;
import org.team14.webty.reviewComment.repository.ReviewCommentRepository;
import org.team14.webty.security.authentication.AuthWebtyUserProvider;
import org.team14.webty.security.authentication.WebtyUserDetails;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentService {
	private final ReviewCommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	private final AuthWebtyUserProvider authWebtyUserProvider;
	private final ReviewService reviewService;

	@Transactional
	@Cacheable(value = "comments", key = "#reviewId")
	public CommentResponse createComment(WebtyUserDetails webtyUserDetails, Long reviewId, CommentRequest request) {
		WebtyUser user = getAuthenticatedUser(webtyUserDetails);
		if (!reviewService.existsReviewById(reviewId)) {
			throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
		}

		Long parentId = request.getParentCommentId();
		Integer depth = 0;  // 기본값은 0 (루트 댓글)

		if (parentId != null) {
			// 부모 댓글이 존재하는지 확인
			ReviewComment parentComment = commentRepository.findById(parentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
			if (parentComment.getDepth() >= 2) {
				throw new BusinessException(ErrorCode.COMMENT_WRITING_RESTRICTED);
			}
			depth = parentComment.getDepth() + 1;  // 부모 댓글의 depth + 1
		}

		ReviewComment comment = ReviewComment.builder()
			.user(user)
			.review(reviewRepository.getReferenceById(reviewId))  // 실제 객체 로딩 없이 참조만 가져옴
			.content(request.getContent())
			.parentId(request.getParentCommentId())
			.mentions(request.getMentions())
			.depth(depth)  // depth 설정
			.build();

		ReviewComment savedComment = commentRepository.save(comment);
		return toResponse(savedComment);
	}

	@Transactional
	@CachePut(value = "comments", key = "#commentId")
	public CommentResponse updateComment(Long commentId, WebtyUserDetails webtyUserDetails, CommentRequest request) {
		WebtyUser user = getAuthenticatedUser(webtyUserDetails);
		ReviewComment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
		}

		comment.updateComment(request.getContent(), request.getMentions());
		return toResponse(comment);
	}

	@Transactional
	@CacheEvict(value = "comments", allEntries = true)
	public void deleteComment(Long commentId, WebtyUserDetails webtyUserDetails) {
		WebtyUser user = getAuthenticatedUser(webtyUserDetails);
		ReviewComment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (!comment.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
		}

		// 대댓글이 있는 경우의 처리 정책 명확화 필요
		List<ReviewComment> childComments = commentRepository.findByParentIdOrderByCommentIdAsc(commentId);
		if (!childComments.isEmpty()) {
			// 정책 1: 대댓글도 모두 삭제
			commentRepository.deleteAll(childComments);
		}
		commentRepository.delete(comment);
	}

	@Cacheable(value = "comments", key = "#reviewId + '_' + #page + '_' + #size")
	public Page<CommentResponse> getCommentsByReviewId(Long reviewId, int page, int size) {
		if (!reviewService.existsReviewById(reviewId)) {
			throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<ReviewComment> commentPage = commentRepository
			.findAllByReviewIdOrderByDepthAndCommentId(reviewId, pageable);

		// 부모 댓글 ID를 키로 사용하는 Map 초기화
		Map<Long, List<CommentResponse>> commentMap = new HashMap<>();
		List<CommentResponse> rootComments = commentPage.getContent().stream()
			.filter(comment -> {
				if (comment.getParentId() != null) {
					commentMap
						.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>())
						.add(toResponse(comment));
					return false;
				}
				return true;
			})
			.map(ReviewCommentMapper::toResponse)
			.collect(Collectors.toList());

		// 각 루트 댓글에 대해 자식 댓글들을 설정
		rootComments.forEach(root -> setChildComments(root, commentMap));
		return new PageImpl<>(rootComments, pageable, commentPage.getTotalElements());
	}

	private void setChildComments(CommentResponse parent, Map<Long, List<CommentResponse>> commentMap) {
		List<CommentResponse> children = commentMap.getOrDefault(parent.getCommentId(), new ArrayList<>());
		parent.setChildComments(children);
		children.forEach(child -> setChildComments(child, commentMap));
	}

	private WebtyUser getAuthenticatedUser(WebtyUserDetails webtyUserDetails) {
		return authWebtyUserProvider.getAuthenticatedWebtyUser(webtyUserDetails);
	}
}
