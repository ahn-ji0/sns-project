package com.spring.snsproject.service;

import com.spring.snsproject.domain.AlarmType;
import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.comment.CommentDto;
import com.spring.snsproject.domain.dto.comment.CommentEditRequest;
import com.spring.snsproject.domain.dto.comment.CommentWriteRequest;
import com.spring.snsproject.domain.dto.post.PostDto;
import com.spring.snsproject.domain.dto.post.PostEditRequest;
import com.spring.snsproject.domain.dto.post.PostWriteRequest;
import com.spring.snsproject.domain.entity.*;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final AlarmRepository alarmRepository;

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
    public PostDto write(PostWriteRequest postWriteRequest, String userName){
        User user = getUserByUserName(userName);

        Post savedPost = postRepository.save(postWriteRequest.toEntity(user));
        return Post.of(savedPost);
    }

    public Page<PostDto> getAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostDto> postGetResponses = posts.map(post -> Post.of(post));

        return postGetResponses;
    }

    public PostDto getOne(Long postId) {
        Post savedPost = getPostById(postId);
        return Post.of(savedPost);
    }

    public PostDto edit(Long postId, PostEditRequest postEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user,savedPost.getUser().getUserName());

        //수정
        savedPost.editPost(postEditRequest.getTitle(), postEditRequest.getBody());

        Post editedPost = postRepository.save(savedPost);
        return Post.of(editedPost);
    }

    public Long delete(Long postId, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        checkAuthority(user, savedPost.getUser().getUserName());

        //삭제
        postRepository.delete(savedPost);
        return savedPost.getId();
    }

    public Page<PostDto> myFeed(Pageable pageable, String userName) {
        User user = getUserByUserName(userName);

        Page<Post> posts = postRepository.findByUser(user, pageable);
        Page<PostDto> postDtos = posts.map(post -> Post.of(post));
        return postDtos;
    }

    public CommentDto writeComment(Long postId, CommentWriteRequest commentWriteRequest, String userName) {
        User user = getUserByUserName(userName);

        Post savedPost = getPostById(postId);

        Comment savedComment = commentRepository.save(commentWriteRequest.toEntity(user,savedPost));
        return Comment.of(savedComment);
    }

    public Page<CommentDto> getComments(Long postId, Pageable pageable) {
        Post savedPost = getPostById(postId);

        Page<Comment> comments = commentRepository.findByPost(savedPost, pageable);
        Page<CommentDto> commentDtos = comments.map(comment -> Comment.of(comment));
        return commentDtos;
    }

    public CommentDto editComment(Long postId, Long commentId, CommentEditRequest commentEditRequest, String userName) {
        User user = getUserByUserName(userName);

        Comment savedComment = getCommentById(commentId);

        checkAuthority(user, savedComment.getUser().getUserName());

        //수정
        savedComment.editComment(commentEditRequest.getComment());

        Comment editedComment = commentRepository.save(savedComment);
        return Comment.of(editedComment);
    }

    public Long deleteComment(Long postId, Long commentId, String userName) {
        User user = getUserByUserName(userName);

        Comment savedComment = getCommentById(commentId);

        checkAuthority(user, savedComment.getUser().getUserName());

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

        likesRepository.save(Likes.builder()
                .post(savedPost)
                .user(user)
                .build());
    }

    public int getLikes(Long postId) {
        Post savedPost = getPostById(postId);

        return savedPost.getLikes().size();
    }

    public void alarm(Object object) {
        Post post = null;
        User fromUser = null;
        AlarmType alarmType = null;

        if(object instanceof Comment){
            Comment comment = (Comment) object;
            post = comment.getPost();
            fromUser = comment.getUser();
            alarmType = AlarmType.NEW_COMMENT_ON_POST;
        } else if(object instanceof Likes) {
            Likes likes = (Likes) object;
            post = likes.getPost();
            fromUser = likes.getUser();
            alarmType = AlarmType.NEW_LIKE_ON_POST;
        }

        Alarm alarm = Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType.name())
                .fromUserId(fromUser.getId())
                .targetId(post.getId())
                .text(alarmType.getMessage())
                .build();

        alarmRepository.save(alarm);
    }
}
