package com.spring.snsproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.AlarmType;
import com.spring.snsproject.domain.dto.alarm.AlarmGetResponse;
import com.spring.snsproject.service.AlarmService;
import com.spring.snsproject.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmRestController.class)
class AlarmRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlarmService alarmService;

    @Autowired
    ObjectMapper objectMapper;

    private AlarmGetResponse alarmGetResponse1;
    private AlarmGetResponse alarmGetResponse2 ;

    @BeforeEach
    void setUp() {
        alarmGetResponse1 = new AlarmGetResponse(1l, AlarmType.NEW_COMMENT_ON_POST, 1L,1L, DateUtils.dateFormat(new Timestamp(100000000)), DateUtils.dateFormat(new Timestamp(100000000)));
        alarmGetResponse2 = new AlarmGetResponse(2l, AlarmType.NEW_LIKE_ON_POST, 1L,1L, DateUtils.dateFormat(new Timestamp(1000000)), DateUtils.dateFormat(new Timestamp(1000000)));
    }
    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    @WithMockUser
    void getAlarmSuccess() throws Exception {

        given(alarmService.getMyAlarms(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alarms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ??????????????? ?????? ??????")
    @WithAnonymousUser
    void getAlarmFail() throws Exception {

        given(alarmService.getMyAlarms(any(), any())).willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alarms"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}