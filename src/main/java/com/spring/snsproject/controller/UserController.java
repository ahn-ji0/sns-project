package com.spring.snsproject.controller;

import com.spring.snsproject.domain.dto.UserDto;
import com.spring.snsproject.domain.dto.UserJoinRequest;
import com.spring.snsproject.domain.dto.UserJoinResponse;
import com.spring.snsproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/join")
    public ResponseEntity join(@RequestBody UserJoinRequest userJoinRequest){
        UserDto userDto = userService.join(userJoinRequest);
        return ResponseEntity.ok().body(new UserJoinResponse(userDto.getUserName()));
    }
}
