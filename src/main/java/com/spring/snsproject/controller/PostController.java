package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.post.PostDto;
import com.spring.snsproject.domain.dto.post.PostGetResponse;
import com.spring.snsproject.domain.dto.post.PostResponse;
import com.spring.snsproject.domain.dto.post.PostWriteRequest;
import com.spring.snsproject.service.PostService;
import com.spring.snsproject.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
        Page<PostDto> postDtos = postService.getAll(pageable);
        Page<PostGetResponse> posts = postDtos.map(postDto ->
                new PostGetResponse(postDto.getId(), postDto.getTitle(), postDto.getBody(), postDto.getUserName(),
                        DateUtils.dateFormat(postDto.getCreatedAt()), DateUtils.dateFormat(postDto.getLastModifiedAt())));
        model.addAttribute("posts", posts);
        model.addAttribute("previousPage", posts.previousOrFirstPageable().getPageNumber());
        model.addAttribute("nextPage",posts.nextOrLastPageable().getPageNumber());
        return "posts/list";
    }

    @GetMapping("/{postId}")
    public String getOne(@PathVariable Long postId, Model model){
        PostDto postDto = postService.getOne(postId);
        PostGetResponse post = new PostGetResponse(postDto.getId(), postDto.getTitle(), postDto.getBody(), postDto.getUserName(),
                DateUtils.dateFormat(postDto.getCreatedAt()), DateUtils.dateFormat(postDto.getLastModifiedAt()));
        model.addAttribute("post", post);
        return "posts/show";
    }
}
