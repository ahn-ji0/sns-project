package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.following.FollowingResponse;
import com.spring.snsproject.domain.dto.following.FollowingDto;
import com.spring.snsproject.service.FollowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/followings")
@RequiredArgsConstructor
public class FollowingRestController {

    private final FollowingService followingService;

    @PostMapping("/{userId}")
    public Response follow(@PathVariable Long userId, Authentication authentication){
        String message = followingService.follow(userId, authentication.getName());
        return Response.success(new FollowingResponse(message));
    }
}
