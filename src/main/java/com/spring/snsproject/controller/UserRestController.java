package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.rolechange.RoleChangeRequest;
import com.spring.snsproject.domain.dto.rolechange.RoleChangeResponse;
import com.spring.snsproject.domain.dto.token.TokenResponse;
import com.spring.snsproject.domain.dto.user.UserJoinRequest;
import com.spring.snsproject.domain.dto.user.UserJoinResponse;
import com.spring.snsproject.domain.dto.user.UserLoginRequest;
import com.spring.snsproject.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;
    @PostMapping("/join")
    @ApiOperation(value="회원가입 기능", notes ="유저 이름과 비밀번호를 입력하세요.")
    public Response join(@RequestBody UserJoinRequest userJoinRequest){
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest);
        return Response.success(userJoinResponse);
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인 기능", notes = "가입했던 유저 이름과 비밀번호를 입력하세요.")
    public Response login(@RequestBody UserLoginRequest userLoginRequest){
        TokenResponse tokenResponse = userService.login(userLoginRequest);
        return Response.success(tokenResponse);
    }

    @PostMapping("{id}/role/change")
    public Response changeRole(@PathVariable Long id, @RequestBody RoleChangeRequest roleChangeRequest){
        RoleChangeResponse roleChangeResponse = userService.changeRole(id, roleChangeRequest);
        return Response.success(roleChangeResponse);
    }
}
