package com.spring.snsproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
}
