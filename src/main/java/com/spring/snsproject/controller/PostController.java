package com.spring.snsproject.controller;

import com.spring.snsproject.domain.dto.post.PostEditRequest;
import com.spring.snsproject.domain.dto.post.PostGetResponse;
import com.spring.snsproject.domain.dto.post.PostResponse;
import com.spring.snsproject.domain.dto.post.PostWriteRequest;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.service.PostService;
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
    public String getAll(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page<PostGetResponse> posts = postService.getAll(pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("previousPage", posts.previousOrFirstPageable().getPageNumber());
        model.addAttribute("nextPage",posts.nextOrLastPageable().getPageNumber());
        return "posts/list";
    }

    @GetMapping("/{postId}")
    public String getOne(@PathVariable Long postId, Model model){
        PostGetResponse post = postService.getOne(postId);
        model.addAttribute("post", post);
        return "posts/show";
    }

    @GetMapping("/new")
    public String write(){
        return "posts/new";
    }

    @PostMapping ("/new")
    public String postNew(PostWriteRequest postWriteRequest, Authentication authentication, Model model){
        postService.write(postWriteRequest, authentication.getName());
        return "redirect:/posts/list";
    }

    @GetMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId, Model model){
        model.addAttribute("postId", postId);
        return "posts/edit";
    }

    @PutMapping("/{postId}")
    public String modify(@PathVariable Long postId, PostEditRequest postEditRequest, Authentication authentication){
        postService.edit(postId, postEditRequest, authentication.getName());
        return "redirect:/posts/list";
    }

    @DeleteMapping("/{postId}")
    public String delete(@PathVariable Long postId, Authentication authentication){
        postService.delete(postId, authentication.getName());
        return "redirect:/posts/list";
    }
}

