package com.spring.snsproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostWriteResponse {
    private String message;
    private Long postId;
}
