package com.spring.snsproject.service;

import com.spring.snsproject.domain.dto.UserDto;
import com.spring.snsproject.domain.dto.UserJoinRequest;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto join(UserJoinRequest userJoinRequest){

        // 유저네임 중복 확인
        userRepository.findByUserName(userJoinRequest.getUserName()).ifPresent(user -> {
            throw new AppException(ErrorCode.DUPLICATE_USERNAME, "중복되는 이름의 유저가 존재합니다.");
        });

        // DB에 이름, 비밀번호(인코딩) 저장
        User user = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }
}
