package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class Alarm extends BaseEntity{

    private AlarmType alarmType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long fromUserId;
    private Long targetId;
    private String text;
}
