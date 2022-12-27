package com.spring.snsproject.service;


import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.PostDto;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class PostServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PostRepository postRepository = Mockito.mock(PostRepository.class);

    private PostService postService;

    @BeforeEach
    void setUp(){
        postService = new PostService(userRepository, postRepository);
    }

    @Test
    @DisplayName("포스트 등록 성공 테스트")
    void writeSuccess(){
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.USER).build();

        PostWriteRequest postWriteRequest = new PostWriteRequest("제목입니다.","내용입니다.");

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
        assertEquals("안지영",postDto.getUserName());
        assertEquals("제목입니다.",postDto.getTitle());
        assertEquals("내용입니다.",postDto.getBody());
    }

    @Test
    @DisplayName("포스트 등록 실패 테스트 - 유저 존재하지 않을 때")
    void writeFail() {
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.USER).build();

        PostWriteRequest postWriteRequest = new PostWriteRequest("제목입니다.", "내용입니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.write(postWriteRequest, "안지영");
        });
    }
}
