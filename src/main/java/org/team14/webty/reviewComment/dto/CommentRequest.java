package org.team14.webty.reviewComment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CommentRequest {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String comment;
    private Long parentCommentId;
    private List<String> mentionedUsernames;
} 