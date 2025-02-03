package org.team14.webty.reviewComment.dto;

import lombok.Getter;
import lombok.Setter;
import org.team14.webty.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UserDto user;
    private Long commentId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer depth;
    private Long parentId;
    
    @Builder.Default
    private List<CommentResponse> childComments = new ArrayList<>();
} 