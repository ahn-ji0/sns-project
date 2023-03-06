package com.spring.snsproject.service;

import com.spring.snsproject.domain.dto.following.FollowingDto;
import com.spring.snsproject.domain.entity.Following;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.FollowingRepository;
import com.spring.snsproject.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowingService {

    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;

    public String follow(Long userId, String userName) {
        User fromUser = userRepository.findByUserName(userName).orElseThrow(() ->
            new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 유저입니다."));

        User toUser = userRepository.findById(userId).orElseThrow(() ->
            new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 유저입니다."));

        Optional<Following> following = followingRepository.findByFromUserAndToUser(fromUser, toUser);
        if(following.isEmpty()){
            followingRepository.save(Following.builder().fromUser(fromUser).toUser(toUser).build());
            return "팔로우하였습니다.";
        } else {
            followingRepository.delete(following.get());
            return "팔로우를 취소하였습니다.";
        }
    }
}
