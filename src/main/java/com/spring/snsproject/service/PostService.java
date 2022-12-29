package com.spring.snsproject.service;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.*;
import com.spring.snsproject.domain.entity.Comment;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.CommentRepository;
import com.spring.snsproject.repository.PostRepository;
import com.spring.snsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostDto write(PostWriteRequest postWriteRequest, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userName)));

        Post savedPost = postRepository.save(postWriteRequest.toEntity(user));
        return Post.of(savedPost);
    }

    public PostDto getOne(Long postId) {
        Post savedPost = postRepository.findById(postId).orElseThrow(()
                -> new AppException(ErrorCode.POST_NOT_FOUND, String.format("%s번 포스트는 존재하지 않습니다.",postId)));
        return Post.of(savedPost);
    }

    public PostDto edit(Long postId, PostEditRequest postEditRequest, String userName, Collection<? extends GrantedAuthority> authorities) {
        //유저 존재 여부
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userName)));

        // 포스트 존재 여부
        Post savedPost = postRepository.findById(postId).orElseThrow(()
                -> new AppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트는 존재하지 않습니다.",postId)));

        // 유저 일치 여부(권한)
        if(!authorities.stream().findFirst().get().getAuthority().equals(UserRole.ROLE_ADMIN.toString()) && !userName.equals(savedPost.getUser().getUserName())){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("%s님은 해당 포스트를 수정할 수 없습니다.",userName));
        }

        // 수정, 저장
        savedPost.editPost(postEditRequest.getTitle(), postEditRequest.getBody());

        Post editedPost = postRepository.save(savedPost);
        return Post.of(editedPost);
    }

    public Long delete(Long postId, String userName, Collection<? extends GrantedAuthority> authorities) {
        //유저 존재 여부
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userName)));

        // 포스트 존재 여부
        Post savedPost = postRepository.findById(postId).orElseThrow(()
                -> new AppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트는 존재하지 않습니다.",postId)));

        // 유저 일치 여부(권한)
        if(!authorities.stream().findFirst().get().getAuthority().equals(UserRole.ROLE_ADMIN.toString()) && !userName.equals(savedPost.getUser().getUserName())){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("%s님은 해당 포스트를 삭제할 수 없습니다.",userName));
        }
        // 삭제
        postRepository.delete(savedPost);

        return savedPost.getId();
    }

    public Page<PostDto> getAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostDto> postGetResponses = posts.map(post -> Post.of(post));

        return postGetResponses;
    }

    public CommentDto writeComment(Long postId, CommentWriteRequest commentWriteRequest, String userName) {
        // 유저 존재 여부
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userName)));
        // 포스트 존재 여부
        Post savedPost = postRepository.findById(postId).orElseThrow(()
                -> new AppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트는 존재하지 않습니다.",postId)));
        Comment savedComment;
        try {
            savedComment = commentRepository.save(commentWriteRequest.toEntity(user,savedPost));
        } catch (Exception e) {
            throw new AppException(ErrorCode.DATABASE_ERROR, "DB에러가 발생하여 댓글을 저장할 수 없습니다.");
        }
        return Comment.of(savedComment);
    }
}
