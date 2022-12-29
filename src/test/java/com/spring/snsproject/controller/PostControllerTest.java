package com.spring.snsproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.PostEditRequest;
import com.spring.snsproject.domain.dto.PostWriteRequest;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.service.PostService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    private PostDto postDto1;
    private PostDto postDto2;
    @BeforeEach
    void setUp() {
        postDto1 = new PostDto(1l, "name_1", "title_1", "body_1", new Timestamp(100000000), new Timestamp(100000000));
        postDto2 = new PostDto(2l, "name_2", "title_2", "body_2", new Timestamp(1000000), new Timestamp(1000000));
    }

    @Test
    @DisplayName("포스트 상세 조회 성공 테스트")
    @WithMockUser
    void getSuccess() throws Exception {
        Long postId = 1l;

        given(postService.getOne(postId)).willReturn(postDto1);

        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.title").exists())
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 조회 성공 테스트")
    @WithMockUser
    void getAllSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        List<PostDto> postDtos = new ArrayList<>();
        postDtos.add(postDto1);
        postDtos.add(postDto2);

        given(postService.getAll(pageable)).willReturn(new PageImpl<>(postDtos));

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content[0].id").value(1l))
                .andExpect(jsonPath("$.result.content[1].id").value(2l))
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 작성 성공 테스트")
    @WithMockUser
    void writeSuccess() throws Exception {
        long postId = 1l;
        PostWriteRequest request = new PostWriteRequest("제목입니다.", "내용입니다.");

        given(postService.write(any(), any())).willReturn(postDto1);

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 작성 실패 테스트 - 인증 실패")
    @WithMockUser
    void writeFail() throws Exception {
        long postId = 1l;
        PostWriteRequest request = new PostWriteRequest("제목입니다.", "내용입니다.");

        given(postService.write(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "접근 권한이 없습니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 수정 성공 테스트")
    @WithMockUser
    void editSuccess() throws Exception {

        PostEditRequest request = new PostEditRequest("제목입니다.", "내용입니다.");

        given(postService.edit(any(), any(), any(), any())).willReturn(postDto1);

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print());
    }
    @Test
    @DisplayName("포스트 수정 실패 테스트 - 데이터 베이스 에러")
    @WithMockUser
    void editFail() throws Exception {

        PostEditRequest request = new PostEditRequest("제목입니다.", "내용입니다.");

        given(postService.edit(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 작성자 불일치")
    @WithMockUser
    void editFail2() throws Exception {

        PostEditRequest request = new PostEditRequest("제목입니다.", "내용입니다.");

        given(postService.edit(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "유저와 작성자가 일치하지 않습니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 인증 실패")
    @WithMockUser
    void editFail3() throws Exception {

        PostEditRequest request = new PostEditRequest("제목입니다.", "내용입니다.");

        given(postService.edit(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "인증에 실패했습니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 성공 테스트")
    @WithMockUser
    void deleteSuccess() throws Exception {

        given(postService.delete(any(), any(), any())).willReturn(postDto1.getId());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 데이터 베이스 에러")
    @WithMockUser
    void deleteFail() throws Exception {

        given(postService.delete(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 작성자 불일치")
    @WithMockUser
    void deleteFail2() throws Exception {

        given(postService.delete(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "유저와 작성자가 일치하지 않습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 인증 실패")
    @WithMockUser
    void deleteFail3() throws Exception {

        given(postService.delete(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "인증에 실패했습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}