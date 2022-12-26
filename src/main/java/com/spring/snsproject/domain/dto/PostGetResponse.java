package com.spring.snsproject.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@AllArgsConstructor
@Getter
@Builder
public class PostGetResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private String createdAt;
    private String lastModifiedAt;


    public static PostGetResponse of(PostDto postDto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        return PostGetResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .body(postDto.getBody())
                .userName(postDto.getUserName())
                .createdAt(format.format(postDto.getCreatedAt()))
                .lastModifiedAt(format.format(postDto.getLastModifiedAt()))
                .build();
    }
}
