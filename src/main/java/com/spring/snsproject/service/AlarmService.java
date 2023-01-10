package com.spring.snsproject.service;

import com.spring.snsproject.domain.AlarmType;
import com.spring.snsproject.domain.dto.alarm.AlarmGetResponse;
import com.spring.snsproject.domain.entity.*;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.repository.AlarmRepository;
import com.spring.snsproject.repository.UserRepository;
import com.spring.snsproject.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND,"존재하지 않는 유저입니다."));
    }

    public void alarm(Object object) {
        Post post = null;
        User fromUser = null;
        AlarmType alarmType = null;

        if(object instanceof Comment){
            Comment comment = (Comment) object;
            post = comment.getPost();
            fromUser = comment.getUser();
            alarmType = AlarmType.NEW_COMMENT_ON_POST;
        } else if(object instanceof Likes) {
            Likes likes = (Likes) object;
            post = likes.getPost();
            fromUser = likes.getUser();
            alarmType = AlarmType.NEW_LIKE_ON_POST;
        }

        Alarm alarm = Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType)
                .fromUserId(fromUser.getId())
                .targetId(post.getId())
                .text(alarmType.getMessage())
                .build();

        alarmRepository.save(alarm);
    }

    public Page<AlarmGetResponse> getMyAlarms(Pageable pageable, String userName) {
        User user = getUserByUserName(userName);

        Page<Alarm> alarms = alarmRepository.findByUser(user, pageable);
        Page<AlarmGetResponse> alarmResponses = alarms.map(alarm -> new AlarmGetResponse(alarm.getId(), alarm.getAlarmType(), alarm.getFromUserId(), alarm.getTargetId(),
                alarm.getText(), DateUtils.dateFormat(alarm.getCreatedAt())));
        return alarmResponses;
    }
}
