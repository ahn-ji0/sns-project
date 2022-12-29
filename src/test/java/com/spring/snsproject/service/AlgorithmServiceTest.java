package com.spring.snsproject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmServiceTest {

    AlgorithmService algorithmService = new AlgorithmService();

    @Test
    @DisplayName("자릿수 합 테스트")
    void sumOfDigit(){
        assertEquals(21,algorithmService.sumOfDigit(687));
        assertEquals(22,algorithmService.sumOfDigit(787));
        assertEquals(0,algorithmService.sumOfDigit(0));
        assertEquals(5,algorithmService.sumOfDigit(11111));
    }

}