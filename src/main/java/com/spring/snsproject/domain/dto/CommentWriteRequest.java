package com.spring.snsproject.domain.dto;

import com.spring.snsproject.domain.entity.Comment;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CommentWriteRequest {
    private String comment;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .post(post)
                .user(user)
                .comment(this.comment)
                .build();
    }
}
