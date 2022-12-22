package com.spring.snsproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Builder
public class PostGetResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;


    public static PostGetResponse of(PostDto postDto) {
        return PostGetResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .body(postDto.getBody())
                .userName(postDto.getUserName())
                .createdAt(postDto.getCreatedAt())
                .lastModifiedAt(postDto.getLastModifiedAt())
                .build();
    }
}
