package com.spring.snsproject.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CommentGetResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private String createdAt;
}
