package com.spring.snsproject.domain.dto.following;

import com.spring.snsproject.domain.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FollowingDto {

    private UserDto fromUser;
    private UserDto toUser;
}
