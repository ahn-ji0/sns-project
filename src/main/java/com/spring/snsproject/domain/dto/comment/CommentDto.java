package com.spring.snsproject.domain.dto.comment;

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
    private String userName;
    private String comment;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
}
