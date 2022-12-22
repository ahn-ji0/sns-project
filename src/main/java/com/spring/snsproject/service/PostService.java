package com.spring.snsproject.service;

import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.PostWriteRequest;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.PostRepository;
import com.spring.snsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostDto write(PostWriteRequest postWriteRequest, String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userName)));

        Post savedPost = postRepository.save(postWriteRequest.toEntity(user));
        return Post.of(savedPost);
    }
}
