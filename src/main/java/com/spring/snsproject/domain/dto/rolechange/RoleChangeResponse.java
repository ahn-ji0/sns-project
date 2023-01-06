package com.spring.snsproject.domain.dto.rolechange;

import com.spring.snsproject.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoleChangeResponse {
    private Long id;
    private String userName;
    private UserRole userRole;
}
