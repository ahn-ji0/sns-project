package com.spring.snsproject.domain.dto;

import com.spring.snsproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    private String userName;
    private String password;

    public User toEntity(String encodedPassword){
        return User.builder()
                .userName(this.userName)
                .password(encodedPassword)
                .build();
    }
}
