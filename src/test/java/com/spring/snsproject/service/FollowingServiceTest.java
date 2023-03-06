package com.spring.snsproject.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.spring.snsproject.domain.entity.Following;
import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.repository.FollowingRepository;
import com.spring.snsproject.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FollowingServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private FollowingRepository followingRepository = Mockito.mock(FollowingRepository.class);

    private FollowingService followingService;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        followingService = new FollowingService(userRepository,followingRepository);
        user1 = User.builder().id(1l).userName("test1").build();
        user2 = User.builder().id(2l).userName("test2").build();
    }

    @Test
    @DisplayName("팔로우 성공 테스트")
    void followSuccess() {

        Following following = new Following(user1, user2);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findByUserName(any())).thenReturn(Optional.of(user1));

        Mockito.when(followingRepository.findByFromUserAndToUser(any(),any()))
                .thenReturn(Optional.ofNullable(null));

        Mockito.when(followingRepository.save(any())).thenReturn(following);

        String message = followingService.follow(user2.getId(), user1.getUserName());
        assertEquals(message, "팔로우하였습니다.");

    }

    @Test
    @DisplayName("팔로우 취소 테스트")
    void followCancel() {
        Following following = new Following(user1, user2);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findByUserName(any())).thenReturn(Optional.of(user1));

        Mockito.when(followingRepository.findByFromUserAndToUser(any(),any()))
               .thenReturn(Optional.of(following));

        doNothing().when(followingRepository).delete(any());

        String message = followingService.follow(user2.getId(), user1.getUserName());
        assertEquals(message, "팔로우를 취소하였습니다.");
    }
}
