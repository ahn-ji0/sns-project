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

        given(postService.getAll(any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 작성 성공 테스트")
    @WithMockUser
    void writeSuccess() throws Exception {

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
    @DisplayName("포스트 수정 실패 테스트 - 데이터 베이스 에러")
    @WithMockUser
    void editFail() throws Exception {

        PostEditRequest request = new PostEditRequest("제목입니다.", "내용입니다.");

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

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

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "유저와 작성자가 일치하지 않습니다."));

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

        given(postService.edit(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "인증에 실패했습니다."));

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
    @DisplayName("포스트 삭제 실패 테스트 - 데이터 베이스 에러")
    @WithMockUser
    void deleteFail() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, " "));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 작성자 불일치")
    @WithMockUser
    void deleteFail2() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "유저와 작성자가 일치하지 않습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 인증 실패")
    @WithMockUser
    void deleteFail3() throws Exception {

        given(postService.delete(any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, "인증에 실패했습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 작성 성공 테스트")
    @WithMockUser
    void commentWriteSuccess() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("댓글입니다.");
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
    @DisplayName("댓글 작성 실패 테스트 - 게시물이 존재하지 않은 경우")
    @WithMockUser
    void commentWriteFail() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("댓글입니다.");
        given(postService.writeComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 작성 실패 테스트 - 로그인하지 않은 경우")
    @WithAnonymousUser
    void commentWriteFail2() throws Exception {
        CommentWriteRequest request = new CommentWriteRequest("댓글입니다.");
        given(postService.writeComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    @WithMockUser
    void commentEditSuccess() throws Exception {
        CommentEditRequest request = new CommentEditRequest("댓글입니다.(수정)");
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
    @DisplayName("댓글 수정 실패 테스트 - post가 없는 경우")
    @WithMockUser
    void commentEditFail() throws Exception {
        CommentEditRequest request = new CommentEditRequest("댓글입니다.(수정)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 작성자 불일치")
    @WithMockUser
    void commentEditFail2() throws Exception {
        CommentEditRequest request = new CommentEditRequest("댓글입니다.(수정)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
    @Test
    @DisplayName("댓글 수정 실패 테스트 - 데이터베이스 에러")
    @WithMockUser
    void commentEditFail3() throws Exception {
        CommentEditRequest request = new CommentEditRequest("댓글입니다.(수정)");
        given(postService.editComment(any(), any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 인증 실패")
    @WithAnonymousUser
    void commentEditFail4() throws Exception {
        CommentEditRequest request = new CommentEditRequest("댓글입니다.(수정)");
        given(postService.editComment(any(), any(), any(), any())).willReturn(commentDto1);

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
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
    @DisplayName("댓글 삭제 실패 테스트-포스트 없는 경우")
    @WithMockUser
    void commentDeleteFail() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트-작성자 불일치")
    @WithMockUser
    void commentDeleteFail2() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트-데이터베이스 에러")
    @WithMockUser
    void commentDeleteFail3() throws Exception {
        given(postService.deleteComment(any(), any(), any())).willThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트- 인증 실패")
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
    @DisplayName("댓글 목록 조회 테스트")
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
    @DisplayName("마이피드 조회 성공 테스트")
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
    @DisplayName("마이피드 조회 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void myFeedFail() throws Exception {

        given(postService.myFeed(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 누르기 성공")
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
    @DisplayName("좋아요 누르기 실패 - 해당 포스트가 없을 때")
    @WithMockUser
    void pressLikesFail() throws Exception {
        doThrow(new AppException(ErrorCode.POST_NOT_FOUND,"")).when(postService).pressLikes(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void pressLikesFail2() throws Exception {
        doNothing().when(postService).pressLikes(any(),any());
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}