package com.spring.snsproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ExampleController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok().body("안지영");
    }
    @GetMapping("/hello/{num}")
    public ResponseEntity sumOfDigit(@PathVariable int num){
        int sum = 0;
        while(num!=0){
            sum += num % 10;
            num = (int) num / 10;
        }
        return ResponseEntity.ok().body(sum);
    }
    @GetMapping("/bye")
    public ResponseEntity<String> bye(){
        return ResponseEntity.ok().body("bye");
    }
}
