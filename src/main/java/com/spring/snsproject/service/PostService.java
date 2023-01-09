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
    public void checkAuthority(User user, String writer){
        if(!user.getRole().equals(UserRole.ROLE_ADMIN.toString()) && !user.getUserName().equals(writer)){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("%s님은 해당 포스트를 수정할 수 없습니다.",user.getUserName()));
        }
    }

    @Transactional
    public PostResponse write(PostWriteRequest postWriteRequest, String userName){
        User user = getUserByUserName(userName);

        Post savedPost = postRepository.save(postWriteRequest.toEntity(user));
        return new PostResponse("포스트 등록 완료",savedPost.getId());
    }

    public Page<PostGetResponse> getAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostGetResponse> postGetResponses = posts.map(post ->
                new PostGetResponse(post.getId(), post.getTitle(), post.getBody(), post.getUser().getUserName(),
                        DateUtils.dateFormat(post.getCreatedAt()), DateUtils.dateFormat(post.getLastModifiedAt())));
        return postGetResponses;
    }

    public PostGetResponse getOne(Long postId) {
        Post savedPost = getPostById(postId);
        PostGetResponse postGetResponse = new PostGetResponse(savedPost.getId(), savedPost.getTitle(), savedPost.getBody(), savedPost.getUser().getUserName(),
                DateUtils.dateFormat(savedPost.getCreatedAt()), DateUtils.dateFormat(savedPost.getLastModifiedAt()));
        return postGetResponse;
    }

    @Transactional
    public PostResponse edit(Long postId, PostEditRequest postEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user,savedPost.getUser().getUserName());

        //수정
        savedPost.editPost(postEditRequest.getTitle(), postEditRequest.getBody());

        return new PostResponse("포스트 수정 완료",savedPost.getId());
    }

    @Transactional
    public PostResponse delete(Long postId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user, savedPost.getUser().getUserName());

        //삭제
        commentRepository.deleteAllByPost(savedPost);
        likesRepository.deleteAllByPost(savedPost);
        postRepository.delete(savedPost);

        return new PostResponse("포스트 삭제 완료", postId);
    }

    public Page<PostGetResponse> myFeed(Pageable pageable, String userName) {
        User user = getUserByUserName(userName);

        Page<Post> posts = postRepository.findByUser(user, pageable);
        Page<PostGetResponse> postGetResponses = posts.map(post ->
               new PostGetResponse(post.getId(), post.getTitle(), post.getBody(), post.getUser().getUserName(),
                        DateUtils.dateFormat(post.getCreatedAt()), DateUtils.dateFormat(post.getLastModifiedAt())));
        return postGetResponses;
    }

    @Transactional
    public CommentGetResponse writeComment(Long postId, CommentWriteRequest commentWriteRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        Comment savedComment = commentRepository.save(commentWriteRequest.toEntity(user,savedPost));
        CommentGetResponse commentGetResponse = new CommentGetResponse(savedComment.getId(), savedComment.getComment(), savedComment.getUser().getUserName(),
                savedComment.getPost().getId(), DateUtils.dateFormat(savedComment.getCreatedAt()), DateUtils.dateFormat(savedComment.getLastModifiedAt()));
        return commentGetResponse;
    }

    public Page<CommentGetResponse> getComments(Long postId, Pageable pageable) {
        Post savedPost = getPostById(postId);

        Page<Comment> comments = commentRepository.findByPost(savedPost, pageable);
        Page<CommentGetResponse> commentGetResponses = comments.map(comment ->
                new CommentGetResponse(comment.getId(), comment.getComment(), comment.getUser().getUserName(),
                        comment.getPost().getId(), DateUtils.dateFormat(comment.getCreatedAt()), DateUtils.dateFormat(comment.getLastModifiedAt())));
        return commentGetResponses;
    }

    @Transactional
    public CommentGetResponse editComment(Long postId, Long commentId, CommentEditRequest commentEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Comment savedComment = getCommentById(commentId);

        checkAuthority(user, savedComment.getUser().getUserName());

        //수정
        savedComment.editComment(commentEditRequest.getComment());

        CommentGetResponse commentGetResponse = new CommentGetResponse(savedComment.getId(), savedComment.getComment(), savedComment.getUser().getUserName(),
                savedComment.getPost().getId(), DateUtils.dateFormat(savedComment.getCreatedAt()), DateUtils.dateFormat(savedComment.getLastModifiedAt()));
        return commentGetResponse;
    }

    @Transactional
    public CommentResponse deleteComment(Long postId, Long commentId, String userName) {
        User user = getUserByUserName(userName);

        Comment savedComment = getCommentById(commentId);

        checkAuthority(user, savedComment.getUser().getUserName());

        //삭제
        commentRepository.delete(savedComment);

        return new CommentResponse("댓글 삭제 완료", commentId);
    }

    public void pressLikes(Long postId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        // 중복 확인
        likesRepository.findByUserAndPost(user, savedPost).ifPresent(like -> {
            throw new AppException(ErrorCode.DUPLICATE_LIKES, String.format("%s님은 이미 %d번 포스트에 좋아요를 눌렀습니다.",userName, postId));
        });

        likesRepository.save(Likes.builder()
                .post(savedPost)
                .user(user)
                .build());
    }

    public int getLikes(Long postId) {
        Post savedPost = getPostById(postId);

        int numLikes = likesRepository.countByPost(savedPost);
        return numLikes;
    }
}
