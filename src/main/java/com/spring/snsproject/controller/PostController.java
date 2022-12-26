package com.spring.snsproject.controller;
import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.*;
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
        return Response.success(new PostResponse("포스트 등록 완료",postDto.getId()));
    }

    @GetMapping("/{postId}")
    public Response getOne(@PathVariable Long postId){
        PostDto postDto = postService.getOne(postId);
        return Response.success(PostGetResponse.of(postDto));
    }

    @PutMapping("/{postId}")
    public Response edit(@PathVariable Long postId, @RequestBody PostEditRequest postEditRequest, Authentication authentication){
        PostDto postDto = postService.edit(postId, postEditRequest, authentication.getName());
        return Response.success(new PostResponse("포스트 수정 완료",postDto.getId()));
    }
}
