package com.spring.snsproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저가 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자 권한이 없습니다." ),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러" ),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다." ),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 구조의 토큰입니다." ),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다." ),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "존재하지 않는 Role입니다." ),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 없습니다."),
    DUPLICATE_LIKES(HttpStatus.CONFLICT, "좋아요가 중복됩니다."),
    UNMATCHED(HttpStatus.BAD_REQUEST, "매칭이 되지 않습니다.");

    private HttpStatus httpStatus;
    private String errorMessage;
}
