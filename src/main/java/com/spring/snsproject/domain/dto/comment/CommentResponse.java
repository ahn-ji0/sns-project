package com.spring.snsproject.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentResponse {
    private String message;
    private Long id;
}
