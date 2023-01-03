package com.spring.snsproject.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.snsproject.domain.Response;
import com.spring.snsproject.domain.dto.ErrorResponse;
import com.spring.snsproject.exception.AppException;
import com.spring.snsproject.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String e = (String) request.getAttribute("exception");
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = null;
        if(e == null){
            errorCode = ErrorCode.INVALID_TOKEN;
        } else if(e.equals(ErrorCode.WRONG_TOKEN.name())){
            errorCode = ErrorCode.WRONG_TOKEN;
        } else if (e.equals(ErrorCode.EXPIRED_TOKEN.name())){
            errorCode = ErrorCode.EXPIRED_TOKEN;
        } else if (e.equals(ErrorCode.USERNAME_NOT_FOUND.name())){
            errorCode = ErrorCode.USERNAME_NOT_FOUND;
        }

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Response errorResponse = Response.error(new ErrorResponse(errorCode.toString(), errorCode.getErrorMessage()));

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
