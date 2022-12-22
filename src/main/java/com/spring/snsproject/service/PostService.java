package com.spring.snsproject.service;

import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.PostWriteRequest;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.repository.PostRepository;
import com.spring.snsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostDto write(PostWriteRequest postWriteRequest){
        Post savedPost = postRepository.save(postWriteRequest.toEntity());
        return Post.of(savedPost);
    }
}
