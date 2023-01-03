package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class Post extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String body;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments;

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
