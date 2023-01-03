package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.user.UserDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity{

    private String userName;
    private String password;
    private UserRole role;
    private Timestamp deletedAt;

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
