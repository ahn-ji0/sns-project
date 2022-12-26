package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String body;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public static PostDto of(Post savedPost) {
        return PostDto.builder()
                .id(savedPost.getId())
                .userName(savedPost.getUser().getUserName())
                .title(savedPost.getTitle())
                .body(savedPost.getBody())
                .createdAt(savedPost.getCreatedAt())
                .lastModifiedAt(savedPost.getLastModifiedAt())
                .build();
    }
    public void editPost(String title, String body){
        this.title = title;
        this.body = body;
    }
}
