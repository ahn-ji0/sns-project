package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.dto.CommentDto;
import com.spring.snsproject.domain.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class Comment extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String comment;

    public static CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .build();
    }

    public void editComment(String comment) {
        this.comment = comment;
    }
}
