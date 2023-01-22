package com.spring.snsproject.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String userName;
    private String title;
    private String body;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
}
