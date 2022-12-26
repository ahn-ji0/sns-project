package com.spring.snsproject.domain.dto;

import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class PostEditRequest {
    private String title;
    private String body;
}
