package com.spring.snsproject.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExampleController.class)
class ExampleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("자릿수 합 테스트")
    @WithMockUser
    void sumOfDigitsTest() throws Exception {
        mockMvc.perform(get("/api/v1/hello/1111"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(4)))
                .andDo(print());
    }
}