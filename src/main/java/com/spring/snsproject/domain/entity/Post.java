package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
                .userId(savedPost.getUser().getId())
                .title(savedPost.getTitle())
                .body(savedPost.getBody())
                .createdAt(savedPost.getCreatedAt())
                .build();
    }
}
