package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
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

    public User changeRole(UserRole userRole){
        this.role = userRole;
        return this;
    }
}
