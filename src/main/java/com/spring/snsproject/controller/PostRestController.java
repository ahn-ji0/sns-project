package com.spring.snsproject.controller;
import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.comment.*;
import com.spring.snsproject.domain.dto.post.*;
import com.spring.snsproject.service.PostService;
import com.spring.snsproject.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;

    @PostMapping()
    @ApiOperation(value="포스트 작성 기능", notes ="포스트 내용을 입력하세요.")
    public Response write(@RequestBody PostWriteRequest postWriteRequest, Authentication authentication){
        PostDto postDto = postService.write(postWriteRequest, authentication.getName());
        return Response.success(new PostResponse("포스트 등록 완료",postDto.getId()));
    }

    @GetMapping()
    @ApiOperation(value="포스트 조회 기능")
    public Response getAll(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostDto> postDtos = postService.getAll(pageable);

        return Response.success(postDtos.map(post ->
                new PostGetResponse(post.getId(), post.getTitle(), post.getBody(), post.getUserName(),
                        DateUtils.dateFormat(post.getCreatedAt()), DateUtils.dateFormat(post.getLastModifiedAt()))));
    }

    @GetMapping("/{postId}")
    @ApiOperation(value="포스트 상세 조회 기능", notes ="상세 조회하려는 포스트의 id를 url에 입력하세요.")
    public Response getOne(@PathVariable Long postId){
        PostDto postDto = postService.getOne(postId);
        return Response.success(new PostGetResponse(postDto.getId(), postDto.getTitle(), postDto.getBody(), postDto.getUserName(),
                DateUtils.dateFormat(postDto.getCreatedAt()), DateUtils.dateFormat(postDto.getLastModifiedAt())));
    }

    @PutMapping("/{postId}")
    @ApiOperation(value="포스트 수정 기능", notes ="수정하려는 포스트의 id를 url에 입력하고, 수정 내용을 입력하세요.")
    public Response edit(@PathVariable Long postId, @RequestBody PostEditRequest postEditRequest, Authentication authentication){
        PostDto postDto = postService.edit(postId, postEditRequest, authentication.getName());
        return Response.success(new PostResponse("포스트 수정 완료",postDto.getId()));
    }

    @DeleteMapping("/{postId}")
    @ApiOperation(value="포스트 삭제 기능", notes ="삭제하려는 포스트의 id를 url에 입력하세요")
    public Response delete(@PathVariable Long postId, Authentication authentication){
        Long deletedId = postService.delete(postId, authentication.getName());
        return Response.success(new PostResponse("포스트 삭제 완료", deletedId));
    }

    @GetMapping("/my")
    @ApiOperation(value="마이 피드 기능", notes ="나의 피드를 확인하세요")
    public Response myFeed(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        Page<PostDto> postDtos = postService.myFeed(pageable, authentication.getName());
        return Response.success(postDtos.map(post ->
                new PostGetResponse(post.getId(), post.getTitle(), post.getBody(), post.getUserName(),
                        DateUtils.dateFormat(post.getCreatedAt()), DateUtils.dateFormat(post.getLastModifiedAt()))));
    }

    @PostMapping("/{postId}/comments")
    @ApiOperation(value="댓글 작성 기능", notes ="댓글을 입력하세요.")
    public Response writeComment(@PathVariable Long postId, @RequestBody CommentWriteRequest commentWriteRequest, Authentication authentication){
        CommentDto commentDto = postService.writeComment(postId, commentWriteRequest, authentication.getName());
        return Response.success(new CommentGetResponse(commentDto.getId(), commentDto.getComment(), commentDto.getUserName(),
                commentDto.getPostId(), DateUtils.dateFormat(commentDto.getCreatedAt()), DateUtils.dateFormat(commentDto.getLastModifiedAt())));
    }

    @GetMapping("/{postId}/comments")
    @ApiOperation(value="댓글 조회 기능")
    public Response getComments(@PathVariable Long postId, @PageableDefault(size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<CommentDto> commentDtos = postService.getComments(postId, pageable);
        return Response.success(commentDtos.map(comment ->
                new CommentGetResponse(comment.getId(), comment.getComment(), comment.getUserName(),
                        comment.getPostId(), DateUtils.dateFormat(comment.getCreatedAt()), DateUtils.dateFormat(comment.getLastModifiedAt()))));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @ApiOperation(value="댓글 수정 기능", notes ="포스트의 id와 수정하려는 댓글의 id를 url에 입력하고, 수정 내용을 입력하세요.")
    public Response editComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentEditRequest commentEditRequest, Authentication authentication){
        CommentDto commentDto = postService.editComment(postId, commentId, commentEditRequest, authentication.getName());
        return Response.success(new CommentGetResponse(commentDto.getId(), commentDto.getComment(), commentDto.getUserName(),
                commentDto.getPostId(), DateUtils.dateFormat(commentDto.getCreatedAt()), DateUtils.dateFormat(commentDto.getLastModifiedAt())));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @ApiOperation(value="댓글 삭제 기능", notes ="포스트의 id와 삭제하려는 댓글의 id를 url에 입력하세요.")
    public Response deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        Long deletedId = postService.deleteComment(postId, commentId, authentication.getName());
        return Response.success(new CommentResponse("댓글 삭제 완료", commentId));
    }

    @PostMapping("/{postId}/likes")
    @ApiOperation(value="좋아요 누르기 기능", notes ="좋아요를 누르려는 포스트의 id를 url에 입력하세요.")
    public Response pressLikes(@PathVariable Long postId, Authentication authentication) {
        postService.pressLikes(postId, authentication.getName());
        return Response.success("좋아요를 눌렀습니다.");
    }

    @GetMapping("/{postId}/likes")
    @ApiOperation(value="좋아요 개수 조회 기능", notes ="좋아요 개수를 조회하려는 포스트의 id를 url에 입력하세요.")
    public Response getLikes(@PathVariable Long postId) {
        int numLikes = postService.getLikes(postId);
        return Response.success(numLikes);
    }
}
