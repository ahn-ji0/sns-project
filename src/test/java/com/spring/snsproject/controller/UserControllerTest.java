package com.spring.snsproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.dto.UserDto;
import com.spring.snsproject.domain.dto.UserJoinRequest;
import com.spring.snsproject.domain.dto.UserLoginRequest;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 테스트")
    @WithMockUser
    void joinSuccess() throws Exception {
        UserJoinRequest request = new UserJoinRequest("개발자","1234");

        given(userService.join(any())).willReturn(UserDto.builder()
                .id(1l)
                .userName(request.getUserName())
                .build());

        mockMvc.perform(post("/api/v1/users/join").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.result.userId").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    @WithMockUser
    void joinFail() throws Exception {
        UserJoinRequest request = new UserJoinRequest("개발자","1234");

        given(userService.join(any())).willThrow(new AppException(ErrorCode.DUPLICATE_USERNAME, ""));

        mockMvc.perform(post("/api/v1/users/join").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void loginSuccess() throws Exception {
        UserLoginRequest request = new UserLoginRequest("개발자","1234");

        given(userService.login(any())).willReturn("test token");

        mockMvc.perform(post("/api/v1/users/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jwt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - userName 존재하지 않음")
    @WithMockUser
    void loginFailUserName() throws Exception {
        UserLoginRequest request = new UserLoginRequest("개발자","1234");

        given(userService.login(any())).willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND,""));

        mockMvc.perform(post("/api/v1/users/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - password 일치하지 않음")
    @WithMockUser
    void loginFailPassword() throws Exception {
        UserLoginRequest request = new UserLoginRequest("개발자","1234");

        given(userService.login(any())).willThrow(new AppException(ErrorCode.INVALID_PASSWORD,""));

        mockMvc.perform(post("/api/v1/users/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").exists())
                .andDo(print());
    }
}