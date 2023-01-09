package com.spring.snsproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.rolechange.RoleChangeRequest;
import com.spring.snsproject.domain.dto.rolechange.RoleChangeResponse;
import com.spring.snsproject.domain.dto.token.TokenResponse;
import com.spring.snsproject.domain.dto.user.UserJoinRequest;
import com.spring.snsproject.domain.dto.user.UserJoinResponse;
import com.spring.snsproject.domain.dto.user.UserLoginRequest;
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

        given(userService.join(any())).willReturn(new UserJoinResponse(1l, "name"));

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

        given(userService.login(any())).willReturn(new TokenResponse("test token"));

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

    @Test
    @DisplayName("role 변경 성공 테스트")
    @WithMockUser
    void changeRoleSuccess() throws Exception {

        RoleChangeRequest request = new RoleChangeRequest("admin");

        given(userService.changeRole(any(), any())).willReturn(new RoleChangeResponse(1l, "name", UserRole.ROLE_ADMIN));

        mockMvc.perform(post("/api/v1/users/1/role/change")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.userRole").value(UserRole.ROLE_ADMIN.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("role 변경 실패 테스트 - 인증 실패")
    @WithMockUser
    void changeRoleFail() throws Exception {

        RoleChangeRequest request = new RoleChangeRequest("admin");

        given(userService.changeRole(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "접근 권한이 없습니다."));

        mockMvc.perform(post("/api/v1/users/1/role/change")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("role 변경 실패 테스트 - 적절하지 않은 role인 경우")
    @WithMockUser
    void changeRoleFail2() throws Exception {

        RoleChangeRequest request = new RoleChangeRequest("super_admin");

        given(userService.changeRole(any(), any())).willThrow(new AppException(ErrorCode.INVALID_ROLE, "admin, user 중 하나를 입력해주세요"));

        mockMvc.perform(post("/api/v1/users/1/role/change")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}