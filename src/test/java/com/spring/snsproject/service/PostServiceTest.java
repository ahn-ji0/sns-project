package com.spring.snsproject.service;


import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.PostEditRequest;
import com.spring.snsproject.domain.dto.PostWriteRequest;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.PostRepository;
import com.spring.snsproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class PostServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PostRepository postRepository = Mockito.mock(PostRepository.class);

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(userRepository, postRepository);
    }

    @Test
    @DisplayName("포스트 등록 성공 테스트")
    void writeSuccess() {
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        PostWriteRequest postWriteRequest = new PostWriteRequest("제목입니다.", "내용입니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.save(any()))
                .thenReturn(Post.builder()
                        .id(1l)
                        .user(user)
                        .title(postWriteRequest.getTitle())
                        .body(postWriteRequest.getBody())
                        .build());

        PostDto postDto = postService.write(postWriteRequest, "안지영");
        assertEquals(user.getUserName(), postDto.getUserName());
        assertEquals(postWriteRequest.getTitle(), postDto.getTitle());
        assertEquals(postWriteRequest.getBody(), postDto.getBody());
    }

    @Test
    @DisplayName("포스트 등록 실패 테스트 - 유저 존재하지 않을 때")
    void writeFail() {

        PostWriteRequest postWriteRequest = new PostWriteRequest("제목입니다.", "내용입니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.write(postWriteRequest, "안지영");
        });
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 유저가 존재하지 않을 때")
    void editFail() {

        PostEditRequest postEditRequests = new PostEditRequest("제목 수정합니다.", "내용 수정합니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.edit(1l, postEditRequests, "안지영", List.of(new SimpleGrantedAuthority(UserRole.ROLE_USER.name())));
        });
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 포스트 존재하지 않을 때")
    void editFail2() {
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        PostEditRequest postEditRequests = new PostEditRequest("제목 수정합니다.", "내용 수정합니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.edit(1l, postEditRequests, user.getUserName(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
        });
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 - 작성자와 유저가 다를 때")
    void editFail3() {
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        User postWrittenUser = User.builder().id(2l).userName("영지안")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l)
                .title("제목입니다.")
                .body("내용입니다.")
                .user(postWrittenUser)
                .build();

        PostEditRequest postEditRequests = new PostEditRequest("제목 수정합니다.", "내용 수정합니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        assertThrows(AppException.class, () -> {
            postService.edit(1l, postEditRequests, user.getUserName(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
        });
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 유저가 존재하지 않을 때")
    void deleteFail() {

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.delete(1l, "안지영", List.of(new SimpleGrantedAuthority(UserRole.ROLE_USER.name())));
        });
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 포스트 존재하지 않을 때")
    void deleteFail2() {
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.delete(1l, user.getUserName(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
        });
    }
}
