package com.spring.snsproject.service;


import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.comment.CommentEditRequest;
import com.spring.snsproject.domain.dto.post.PostEditRequest;
import com.spring.snsproject.domain.dto.post.PostResponse;
import com.spring.snsproject.domain.dto.post.PostWriteRequest;
import com.spring.snsproject.domain.entity.Comment;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.repository.CommentRepository;
import com.spring.snsproject.repository.LikesRepository;
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
    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private LikesRepository likesRepository = Mockito.mock(LikesRepository.class);

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(userRepository, postRepository,commentRepository,likesRepository);
    }

    @Test
    @DisplayName("포스트 등록 성공 테스트")
    void writeSuccess() {
        Long postId = 1l;
        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        PostWriteRequest postWriteRequest = new PostWriteRequest("제목입니다.", "내용입니다.");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.save(any()))
                .thenReturn(Post.builder()
                        .id(postId)
                        .user(user)
                        .title(postWriteRequest.getTitle())
                        .body(postWriteRequest.getBody())
                        .build());

        PostResponse postResponse = postService.write(postWriteRequest, "안지영");
        assertEquals(postId, postResponse.getPostId());

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
            postService.edit(1l, postEditRequests, "안지영");
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
            postService.edit(1l, postEditRequests, user.getUserName());
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
            postService.edit(1l, postEditRequests, user.getUserName());
        });
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 - 유저가 존재하지 않을 때")
    void deleteFail() {

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.delete(1l, "안지영");
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
            postService.delete(1l, user.getUserName());
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 유저가 존재하지 않을 때")
    void editCommentFail() {

        CommentEditRequest commentEditRequest = new CommentEditRequest("댓글 수정");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.editComment(1l, 1l, commentEditRequest, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 포스트가 존재하지 않을 때")
    void editCommentFail2() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();


        CommentEditRequest commentEditRequest = new CommentEditRequest("댓글 수정");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));


        assertThrows(AppException.class, () -> {
            postService.editComment(1l, 1l, commentEditRequest, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 댓글이 존재하지 않을 때")
    void editCommentFail3() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();

        CommentEditRequest commentEditRequest = new CommentEditRequest("댓글 수정");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.editComment(1l, 1l, commentEditRequest, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 댓글이 해당 포스트의 것이 아닐때")
    void editCommentFail4() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();

        Post post2 = Post.builder()
                .id(2l).title("제목입니다.").body("내용입니다.")
                .user(user).build();


        Comment comment = Comment.builder()
                .id(1l).comment("댓글").post(post2)
                .user(user).build();

        CommentEditRequest commentEditRequest = new CommentEditRequest("댓글 수정");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.of(comment));

        assertThrows(AppException.class, () -> {
            postService.editComment(1l, 1l, commentEditRequest, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 작성자!=유저")
    void editCommentFail5() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();

        User commentWrittenUser = User.builder().id(2l).userName("영지안")
                .role(UserRole.ROLE_USER).build();

        Comment comment = Comment.builder()
                .id(1l).comment("댓글").post(post)
                .user(commentWrittenUser).build();

        CommentEditRequest commentEditRequest = new CommentEditRequest("댓글 수정");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                        .thenReturn(Optional.of(comment));

        assertThrows(AppException.class, () -> {
            postService.editComment(1l, 1l, commentEditRequest, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 유저가 존재하지 않을 때")
    void deleteCommentFail() {

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.deleteComment(1l, 1l, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 포스트가 존재하지 않을 때")
    void deleteCommentFail2() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));


        assertThrows(AppException.class, () -> {
            postService.deleteComment(1l, 1l, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 댓글이 존재하지 않을 때")
    void deleteCommentFail3() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();


        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(AppException.class, () -> {
            postService.deleteComment(1l, 1l, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 댓글이 해당 포스트의 것이 아닐때")
    void deleteCommentFail4() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();

        Post post2 = Post.builder()
                .id(2l).title("제목입니다.").body("내용입니다.")
                .user(user).build();


        Comment comment = Comment.builder()
                .id(1l).comment("댓글").post(post2)
                .user(user).build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.of(comment));

        assertThrows(AppException.class, () -> {
            postService.deleteComment(1l, 1l, "유저네임");
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 작성자!=유저")
    void deleteCommentFail5() {

        User user = User.builder().id(1l).userName("안지영")
                .role(UserRole.ROLE_USER).build();

        Post post = Post.builder()
                .id(1l).title("제목입니다.").body("내용입니다.")
                .user(user).build();

        User commentWrittenUser = User.builder().id(2l).userName("영지안")
                .role(UserRole.ROLE_USER).build();

        Comment comment = Comment.builder()
                .id(1l).comment("댓글").post(post)
                .user(commentWrittenUser).build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.of(comment));

        assertThrows(AppException.class, () -> {
            postService.deleteComment(1l, 1l, "유저네임");
        });
    }
}
