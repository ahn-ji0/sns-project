package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.UserDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static UserDto of(User user){
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
