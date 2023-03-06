package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.comment.*;
import com.spring.snsproject.domain.dto.post.*;
import com.spring.snsproject.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public String home() {
        return "redirect:/posts/list";
    }

    @GetMapping("/list")
    public String getAll(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<PostDto> posts = postService.getAll(pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("previousPage", posts.previousOrFirstPageable().getPageNumber());
        model.addAttribute("nextPage", posts.nextOrLastPageable().getPageNumber());
        return "posts/list";
    }

    @GetMapping("/{postId}")
    public String getOne(@PathVariable Long postId, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        PostDto post = postService.getOne(postId);
        Page<CommentDto> comments = postService.getComments(postId, pageable);
        model.addAttribute("post", post);
        model.addAttribute("commments", comments);
        return "posts/show";
    }

    @GetMapping("/new")
    public String write() {
        return "posts/new";
    }

    @PostMapping("/new")
    public String postNew(PostWriteRequest postWriteRequest, Authentication authentication, Model model) {
        PostDto postDto = postService.write(postWriteRequest, authentication.getName());
        return String.format("redirect:/posts/%d", postDto.getId());
    }

    @GetMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId, Model model) {
        PostDto post = postService.getOne(postId);
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PutMapping("/{postId}")
    public String modify(@PathVariable Long postId, PostEditRequest postEditRequest, Authentication authentication) {
        PostDto postDto = postService.edit(postId, postEditRequest, authentication.getName());
        return String.format("redirect:/posts/%d", postDto.getId());
    }

    @DeleteMapping("/{postId}")
    public String delete(@PathVariable Long postId, Authentication authentication) {
        postService.delete(postId, authentication.getName());
        return "redirect:/posts/list";
    }

    @GetMapping("/my")
    public String myFeed(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model, Authentication authentication) {
        Page<PostDto> posts = postService.myFeed(pageable, authentication.getName());
        model.addAttribute("posts", posts);
        model.addAttribute("previousPage", posts.previousOrFirstPageable().getPageNumber());
        model.addAttribute("nextPage", posts.nextOrLastPageable().getPageNumber());
        return "posts/list";
    }

    @PostMapping("/{postId}/comments")
    public String writeComment(@PathVariable Long postId, @RequestBody CommentWriteRequest commentWriteRequest, Authentication authentication) {
        postService.writeComment(postId, commentWriteRequest, authentication.getName());
        return String.format("redirect:/posts/%d", postId);
    }

    @GetMapping("/{postId}/comments/{commentId}/edit")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, Model model) {
        CommentDto comment = postService.getOneComment(postId, commentId);
        model.addAttribute("comment", comment);
        return "posts/comment";
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public String modifyComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentEditRequest commentEditRequest, Model model, Authentication authentication) {
        postService.editComment(postId, commentId, commentEditRequest, authentication.getName());
        return String.format("redirect:/posts/%d", postId);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        postService.deleteComment(postId, commentId, authentication.getName());
        return String.format("redirect:/posts/%d", postId);
    }

    @PostMapping("/{postId}/likes")
    public String pressLikes(@PathVariable Long postId, Authentication authentication) {
        postService.pressLikes(postId, authentication.getName());
        return String.format("redirect:/posts/%d", postId);
    }
}


