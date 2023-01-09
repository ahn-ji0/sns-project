package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.UserRole;
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

    public User changeRole(UserRole userRole){
        this.role = userRole;
        return this;
    }
}
