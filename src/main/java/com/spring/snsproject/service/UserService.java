package com.spring.snsproject.service;

import com.spring.snsproject.domain.UserRole;
import com.spring.snsproject.domain.dto.UserDto;
import com.spring.snsproject.domain.dto.UserJoinRequest;
import com.spring.snsproject.domain.dto.UserLoginRequest;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.UserRepository;
import com.spring.snsproject.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private long expiredTimeMs = 1000 * 60 * 60;

    public UserDto join(UserJoinRequest userJoinRequest){

        // 유저네임 중복 확인
        userRepository.findByUserName(userJoinRequest.getUserName()).ifPresent(user -> {
            throw new AppException(ErrorCode.DUPLICATE_USERNAME, String.format("%s는 이미 존재하는 유저네임입니다.",userJoinRequest.getUserName()));
        });

        // DB에 이름, 비밀번호(인코딩) 저장
        User user = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()), UserRole.USER));

        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }

    public String login(UserLoginRequest userLoginRequest) {
        // 유저 네임 존재 확인
        User user = userRepository.findByUserName(userLoginRequest.getUserName()).orElseThrow(()->
                new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s는 존재하지 않는 유저네임입니다.",userLoginRequest.getUserName())));

        // 패스워드 일치 확인
        if(!encoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, String.format("%s님의 비밀번호가 아닙니다.",userLoginRequest.getUserName()));
        }

        // jwt 토큰 발급
        String jwt = JwtUtils.createToken(user.getUserName(), secretKey, expiredTimeMs);

        return jwt;
    }

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND,"존재하지 않는 유저입니다."));
    }
}
