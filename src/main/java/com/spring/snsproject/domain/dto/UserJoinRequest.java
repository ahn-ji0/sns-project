package com.spring.snsproject.domain.dto;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    private String userName;
    private String password;

    public User toEntity(String encodedPassword, UserRole role){
        return User.builder()
                .userName(this.userName)
                .password(encodedPassword)
                .role(role)
                .registeredAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
