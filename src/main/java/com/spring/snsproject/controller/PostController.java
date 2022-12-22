package com.spring.snsproject.controller;
import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.PostDto;
import com.spring.snsproject.domain.dto.PostGetResponse;
import com.spring.snsproject.domain.dto.PostWriteRequest;
import com.spring.snsproject.domain.dto.PostWriteResponse;
import com.spring.snsproject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping()
    public Response write(@RequestBody PostWriteRequest postWriteRequest, Authentication authentication){
        PostDto postDto = postService.write(postWriteRequest, authentication.getName());
        return Response.success(new PostWriteResponse("포스트 등록 완료",postDto.getId()));
    }

    @GetMapping("/{postId}")
    public Response getOne(@PathVariable long postId){
        PostDto postDto = postService.getOne(postId);
        return Response.success(PostGetResponse.of(postDto));
    }
}
