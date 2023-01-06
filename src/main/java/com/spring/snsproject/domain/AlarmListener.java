package com.spring.snsproject.domain;

import com.spring.snsproject.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.persistence.PostPersist;

@Slf4j
public class AlarmListener {

    @Lazy
    @Autowired
    private PostService postService;

    @PostPersist
    public void postPersistComment(Object object){
        postService.alarm(object);
    }
}
