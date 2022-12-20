package com.spring.snsproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response error(String message){
        return new Response(message, null);
    }

    public static <T> Response success(T data){
        return new Response("SUCCESS", data);
    }
}
