package com.spring.snsproject.controller;
import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.*;
import com.spring.snsproject.service.PostService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value="포스트 작성 기능", notes ="포스트 내용을 입력하세요.")
    public Response write(@RequestBody PostWriteRequest postWriteRequest, Authentication authentication){
        PostDto postDto = postService.write(postWriteRequest, authentication.getName());
        return Response.success(new PostResponse("포스트 등록 완료",postDto.getId()));
    }

    @GetMapping("/{postId}")
    @ApiOperation(value="포스트 상세 조회 기능", notes ="상세 조회하려는 포스트의 id를 url에 입력하세요.")
    public Response getOne(@PathVariable Long postId){
        PostDto postDto = postService.getOne(postId);
        return Response.success(PostGetResponse.of(postDto));
    }

    @PutMapping("/{postId}")
    @ApiOperation(value="포스트 수정 기능", notes ="수정하려는 포스트의 id를 url에 입력하고, 수정 내용을 입력하세요.")
    public Response edit(@PathVariable Long postId, @RequestBody PostEditRequest postEditRequest, Authentication authentication){
        PostDto postDto = postService.edit(postId, postEditRequest, authentication.getName(), authentication.getAuthorities());
        return Response.success(new PostResponse("포스트 수정 완료",postDto.getId()));
    }

    @DeleteMapping("/{postId}")
    @ApiOperation(value="포스트 삭제 기능", notes ="삭제하려는 포스트의 id를 url에 입력하세요")
    public Response delete(@PathVariable Long postId, Authentication authentication){
        Long deletedId = postService.delete(postId, authentication.getName(), authentication.getAuthorities());
        return Response.success(new PostResponse("포스트 삭제 완료", deletedId));
    }
}
