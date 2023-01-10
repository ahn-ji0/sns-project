package com.spring.snsproject.domain.dto.alarm;

import com.spring.snsproject.domain.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AlarmGetResponse {
    private Long id;
    private AlarmType alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;
    private String createdAt;
}
