package com.spring.snsproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    @Override
    public String toString() {
        return String.format("%s %s",this.errorCode.getErrorMessage(), this.message);
    }
}
