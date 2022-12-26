package com.spring.snsproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ExampleController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok().body("happy_new_year");
    }
    @GetMapping("/bye")
    public ResponseEntity<String> bye(){
        return ResponseEntity.ok().body("bye");
    }
}
