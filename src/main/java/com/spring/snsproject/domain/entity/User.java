package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.user.UserDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity{

    private String userName;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static UserDto of(User user){
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .lastModifiedAt(user.getLastModifiedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }


    public User changeRole(UserRole userRole){
        this.role = userRole;
        return this;
    }
}
