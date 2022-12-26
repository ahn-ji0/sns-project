package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.*;
import com.spring.snsproject.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    @PostMapping("/join")
    @ApiOperation(value="회원가입 기능", notes ="유저 이름과 비밀번호를 입력하세요.")
    public Response join(@RequestBody UserJoinRequest userJoinRequest){
        UserDto userDto = userService.join(userJoinRequest);
        return Response.success(new UserJoinResponse(userDto.getId(), userDto.getUserName()));
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인 기능", notes = "가입했던 유저 이름과 비밀번호를 입력하세요.")
    public Response login(@RequestBody UserLoginRequest userLoginRequest){
        String jwt = userService.login(userLoginRequest);
        return Response.success(new TokenResponse(jwt));
    }

    @PostMapping("{id}/role/change")
    public Response changeRole(@PathVariable Long id, @RequestBody RoleChangeRequest roleChangeRequest){
        log.info("1");
        UserDto userDto = userService.changeRole(id, roleChangeRequest);
        log.info("2");
        return Response.success(new RoleChangeResponse(userDto.getId(), userDto.getUserName(), userDto.getRole()));
    }
}
