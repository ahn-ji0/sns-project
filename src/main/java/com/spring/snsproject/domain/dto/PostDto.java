package com.spring.snsproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Builder
public class PostDto {
    private Long id;
//    private Long userId;
    private String title;
    private String body;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
}
