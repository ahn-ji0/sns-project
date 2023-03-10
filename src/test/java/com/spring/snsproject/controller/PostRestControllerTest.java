package com.spring.snsproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.dto.comment.*;
import com.spring.snsproject.domain.dto.post.*;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.service.PostService;
import com.spring.snsproject.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
class PostRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    private PostDto postDto1;
    private PostDto postDto2;
    private CommentDto commentDto1;
    private CommentDto commentDto2;

    @BeforeEach
    void setUp() {
        postDto1 = new PostDto(1l, "name_1","title_1", "body_1", new Timestamp(100000000), new Timestamp(100000000));
        postDto2 = new PostDto(2l, "name_2","title_2", "body_2", new Timestamp(1000000), new Timestamp(1000000));
        commentDto1 = new CommentDto(1l, 1l, "user_1", "comment_1", new Timestamp(1000000), new Timestamp(1000000));
        commentDto2 = new CommentDto(2l, 2l, "user_2", "comment_2", new Timestamp(1000000), new Timestamp(1000000));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????????")
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
    @DisplayName("????????? ?????? ?????? ?????????")
    @WithMockUser
    void getAllSuccess() throws Exception {

        given(postService.getAll(any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????????")
    @WithMockUser
    void writeSuccess() throws Exception {

        PostWriteRequest request = new PostWriteRequest("???????????????.", "???????????????.");

        given(postService.write(any(), any())).willReturn(postDto1);

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ?????? ??????")
    @WithMockUser
    void writeFail() throws Exception {

        PostWriteRequest request = new PostWriteRequest("???????????????.", "???????????????.");

        given(postService.write(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "?????? ????????? ????????????."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????????")
    @WithMockUser
    void editSuccess() throws Exception {

        PostEditRequest request = new PostEditRequest("???????????????.", "???????????????.");

        given(postService.edit(any(), any(), any())).willReturn(postDto1);

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
    @DisplayName("????????? ?????? ?????? ????????? - ????????? ????????? ??????")
    @WithMockUser
    void editFail() throws Exception {

        PostEditRequest request = new PostEditRequest("???????????????.", "???????????????.");

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ????????? ?????????")
    @WithMockUser
    void editFail2() throws Exception {

        PostEditRequest request = new PostEditRequest("???????????????.", "???????????????.");

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ???????????? ???????????? ????????????."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ?????? ??????")
    @WithMockUser
    void editFail3() throws Exception {

        PostEditRequest request = new PostEditRequest("???????????????.", "???????????????.");

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ??????????????????."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????????")
    @WithMockUser
    void deleteSuccess() throws Exception {

        Long postId = 1l;

        given(postService.delete(any(), any())).willReturn(postId);

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ????????? ????????? ??????")
    @WithMockUser
    void deleteFail() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ????????? ?????????")
    @WithMockUser
    void deleteFail2() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ???????????? ???????????? ????????????."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? - ?????? ??????")
    @WithMockUser
    void deleteFail3() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ??????????????????."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    @WithMockUser
    void commentWriteSuccess() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("???????????????.");
        given(postService.writeComment(any(), any(), any())).willReturn(commentDto1);

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andDo(print());
    }
    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ???????????? ???????????? ?????? ??????")
    @WithMockUser
    void commentWriteFail() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("???????????????.");
        given(postService.writeComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ??????????????? ?????? ??????")
    @WithAnonymousUser
    void commentWriteFail2() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("???????????????.");
        given(postService.writeComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    @WithMockUser
    void commentEditSuccess() throws Exception {
        CommentEditRequest request = new CommentEditRequest("???????????????.(??????)");
        given(postService.editComment(any(), any(), any(), any())).willReturn(commentDto1);

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - post??? ?????? ??????")
    @WithMockUser
    void commentEditFail() throws Exception {
        CommentEditRequest request = new CommentEditRequest("???????????????.(??????)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ????????? ?????????")
    @WithMockUser
    void commentEditFail2() throws Exception {
        CommentEditRequest request = new CommentEditRequest("???????????????.(??????)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ?????????????????? ??????")
    @WithMockUser
    void commentEditFail3() throws Exception {
        CommentEditRequest request = new CommentEditRequest("???????????????.(??????)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ?????? ??????")
    @WithAnonymousUser
    void commentEditFail4() throws Exception {
        CommentEditRequest request = new CommentEditRequest("???????????????.(??????)");
        given(postService.editComment(any(), any(), any(), any())).willReturn(commentDto1);

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    @WithMockUser
    void commentDeleteSuccess() throws Exception {
        Long commentId = 1l;

        given(postService.deleteComment(any(), any(), any())).willReturn(commentId);

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????-????????? ?????? ??????")
    @WithMockUser
    void commentDeleteFail() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????-????????? ?????????")
    @WithMockUser
    void commentDeleteFail2() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????-?????????????????? ??????")
    @WithMockUser
    void commentDeleteFail3() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????- ?????? ??????")
    @WithAnonymousUser
    void commentDeleteFail4() throws Exception {
        Long commentId = 1l;

        given(postService.deleteComment(any(), any(), any())).willReturn(commentId);

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    @WithMockUser
    void commentListSuccess() throws Exception {

        given(postService.getComments(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????????")
    @WithMockUser
    void myFeedSuccess() throws Exception {

        given(postService.myFeed(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("???????????? ?????? ?????? - ????????? ?????? ?????? ??????")
    @WithAnonymousUser
    void myFeedFail() throws Exception {

        given(postService.myFeed(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    @WithMockUser
    void pressLikeSuccess() throws Exception {
        doNothing().when(postService).pressLikes(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ?????? - ?????? ???????????? ?????? ???")
    @WithMockUser
    void pressLikesFail() throws Exception {
        doThrow(new AppException(ErrorCode.POST_NOT_FOUND,"")).when(postService).pressLikes(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("????????? ????????? ?????? - ????????? ?????? ?????? ??????")
    @WithAnonymousUser
    void pressLikesFail2() throws Exception {
        doNothing().when(postService).pressLikes(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}