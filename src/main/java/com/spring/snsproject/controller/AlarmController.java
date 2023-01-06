package com.spring.snsproject.controller;

import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.AlarmGetResponse;
import com.spring.snsproject.domain.dto.comment.*;
import com.spring.snsproject.domain.dto.post.*;
import com.spring.snsproject.service.AlarmService;
import com.spring.snsproject.service.PostService;
import com.spring.snsproject.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
@Slf4j
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping()
    @ApiOperation(value="알람 조회 기능")
    public Response getMyAlarms(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication){
        Page<AlarmGetResponse> alarms = alarmService.getMyAlarms(pageable, authentication.getName());
        return Response.success(alarms);
    }
}
