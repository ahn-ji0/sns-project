package com.spring.snsproject.service;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.comment.*;
import com.spring.snsproject.domain.dto.post.*;
import com.spring.snsproject.domain.entity.*;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.*;
import com.spring.snsproject.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND,"존재하지 않는 유저입니다."));
    }
    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElseThrow(()
                -> new AppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트는 존재하지 않습니다.",postId)));
    }
    public Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(()
                -> new AppException(ErrorCode.COMMENT_NOT_FOUND, String.format("%d번 댓글은 존재하지 않습니다.",commentId)));
    }

    private static void compareId(Long postId, Comment savedComment) {
        if(savedComment.getPost().getId()!= postId){
            throw new AppException(ErrorCode.UNMATCHED, String.format("%d번 커멘트는 %d번 포스트의 댓글이 아닙니다.",savedComment.getId(), postId));
        }
    }

    public void checkAuthority(User authenticatedUser, User writer){
        if(!authenticatedUser.getRole().equals(UserRole.ROLE_ADMIN.toString()) && !authenticatedUser.getId().equals(writer.getId())){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("%s님은 해당 포스트를 수정할 수 없습니다.",authenticatedUser.getUserName()));
        }
    }

    @Transactional
    public PostDto write(PostWriteRequest postWriteRequest, String userName){
        User user = getUserByUserName(userName);

        Post savedPost = postRepository.save(postWriteRequest.toEntity(user));
        return Post.of(savedPost);
    }

    public Page<PostDto> getAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> Post.of(post));
    }

    public PostDto getOne(Long postId) {
        Post savedPost = getPostById(postId);
        return Post.of(savedPost);
    }

    @Transactional
    public PostDto edit(Long postId, PostEditRequest postEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user,savedPost.getUser());

        //수정
        savedPost.editPost(postEditRequest.getTitle(), postEditRequest.getBody());

        return Post.of(savedPost);
    }

    @Transactional
    public Long delete(Long postId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user, savedPost.getUser());

        //삭제
        commentRepository.deleteAllByPost(savedPost);
        likesRepository.deleteAllByPost(savedPost);
        postRepository.delete(savedPost);

        return savedPost.getId();
    }

    public Page<PostDto> myFeed(Pageable pageable, String userName) {
        User user = getUserByUserName(userName);

        Page<Post> posts = postRepository.findByUser(user, pageable);
        return posts.map(post -> Post.of(post));
    }

    @Transactional
    public CommentDto writeComment(Long postId, CommentWriteRequest commentWriteRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        Comment savedComment = commentRepository.save(commentWriteRequest.toEntity(user,savedPost));
        return Comment.of(savedComment);
    }

    public Page<CommentDto> getComments(Long postId, Pageable pageable) {
        Post savedPost = getPostById(postId);

        Page<Comment> comments = commentRepository.findByPost(savedPost, pageable);
        return comments.map(comment -> Comment.of(comment));
    }

    @Transactional
    public CommentDto editComment(Long postId, Long commentId, CommentEditRequest commentEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        Comment savedComment = getCommentById(commentId);

        compareId(postId, savedComment);

        checkAuthority(user, savedComment.getUser());

        //수정
        savedComment.editComment(commentEditRequest.getComment());

        return Comment.of(savedComment);
    }


    @Transactional
    public Long deleteComment(Long postId, Long commentId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        Comment savedComment = getCommentById(commentId);

        compareId(postId, savedComment);

        checkAuthority(user, savedComment.getUser());

        //삭제
        commentRepository.delete(savedComment);

        return savedComment.getId();
    }

    public void pressLikes(Long postId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        // 중복 확인
        likesRepository.findByUserAndPost(user, savedPost).ifPresent(like -> {
            throw new AppException(ErrorCode.DUPLICATE_LIKES, String.format("%s님은 이미 %d번 포스트에 좋아요를 눌렀습니다.",userName, postId));
        });

        likesRepository.save(Likes.builder().post(savedPost).user(user).build());
    }

    public int getLikes(Long postId) {
        Post savedPost = getPostById(postId);

        int numLikes = likesRepository.countByPost(savedPost);
        return numLikes;
    }
}
