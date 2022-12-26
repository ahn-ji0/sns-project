package com.spring.snsproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("포스트 상세 조회 성공 테스트")
    @WithMockUser
    void getSuccess() throws Exception {
        long postId = 1l;

        given(postService.getOne(postId)).willReturn(PostDto.builder()
                .id(1l)
                .title("제목")
                .body("내용")
                .userName("유저 네임")
                .build());

        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.title").exists())
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.createdAt").isEmpty())
                .andExpect(jsonPath("$.result.lastModifiedAt").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 조회 성공 테스트")
    @WithMockUser
    void getAllSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        List<PostDto> postDtos = new ArrayList<>();
        postDtos.add(new PostDto(1l,"name_1","title_1","body_1", new Timestamp(100000000), null));
        postDtos.add(new PostDto(2l,"name_2","title_2","body_2", new Timestamp(1000000), null));

        given(postService.getAll(pageable)).willReturn(new PageImpl<>(postDtos));

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content[0].id").value(1l))
                .andExpect(jsonPath("$.result.content[1].id").value(2l))
                .andDo(print());
    }
}