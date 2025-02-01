package org.team14.webty.reviewComment.dto;

import lombok.Getter;
import lombok.Setter;
import org.team14.webty.user.dto.UserDto;
import org.team14.webty.reviewComment.entity.ReviewComment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponse {
    private final UserDto user;
    private final Long commentId;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Integer depth;
    private final Long parentId;
    @Setter
    private List<CommentResponse> childComments = new ArrayList<>();

    public CommentResponse(ReviewComment comment) {
        this.user = new UserDto(comment.getUser());
        this.commentId = comment.getCommentId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.depth = comment.getDepth();
        this.parentId = comment.getParentId();
    }
} 