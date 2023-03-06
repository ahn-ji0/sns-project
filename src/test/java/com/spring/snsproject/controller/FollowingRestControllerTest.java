package com.spring.snsproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.comment.CommentDto;
import com.spring.snsproject.domain.dto.following.FollowingDto;
import com.spring.snsproject.domain.dto.post.PostDto;
import com.spring.snsproject.domain.dto.rolechange.RoleChangeRequest;
import com.spring.snsproject.domain.dto.user.UserDto;
import com.spring.snsproject.domain.dto.user.UserJoinRequest;
import com.spring.snsproject.domain.dto.user.UserLoginRequest;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.service.FollowingService;
import com.spring.snsproject.service.UserService;
import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FollowingRestController.class)
class FollowingRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FollowingService followingService;

    @Autowired
    ObjectMapper objectMapper;

    private UserDto userDto1;
    private UserDto userDto2;
    private FollowingDto followingDto;
    @BeforeEach
    void setUp() {
        userDto1 = UserDto.builder().id(1l).build();
        userDto2 =  UserDto.builder().id(2l).build();
        followingDto = new FollowingDto(userDto1, userDto2);
    }

    @Test
    @DisplayName("팔로잉 성공 테스트")
    @WithMockUser
    void followSuccess() throws Exception {

        given(followingService.follow(any(), any())).willReturn("팔로우하였습니다.");

        mockMvc.perform(post("/api/v1/followings/2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("팔로잉 성공 테스트 - 팔로우 취소한 경우")
    @WithMockUser
    void followSuccess2() throws Exception {

        given(followingService.follow(any(), any())).willReturn("팔로우 취소하였습니다.");

        mockMvc.perform(post("/api/v1/followings/2").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
               .andExpect(jsonPath("$.result.message").exists())
               .andDo(print());
    }

    @Test
    @DisplayName("팔로잉 실패 테스트 - 존재하지 않는 유저 id인 경우")
    @WithMockUser
    void followFail() throws Exception {

        given(followingService.follow(any(), any())).willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND,""));

        mockMvc.perform(post("/api/v1/followings/2").with(csrf()))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.resultCode").value("ERROR"))
               .andDo(print());
    }

}