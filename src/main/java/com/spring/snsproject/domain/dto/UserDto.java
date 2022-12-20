package com.spring.snsproject.domain.dto;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@Builder
public class UserDto {
    private Long id;
    private String userName;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
}
