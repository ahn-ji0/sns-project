package com.spring.snsproject.configuration;

import com.spring.snsproject.domain.entity.User;
import com.spring.snsproject.exception.ErrorCode;
import com.spring.snsproject.service.UserService;
import com.spring.snsproject.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization:{}", authorizationHeader);

        // 토큰이 없는 경우 제외
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            try {
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getErrorMessage());
            }
        }

        try {
            String token = authorizationHeader.split(" ")[1];

            String userName = JwtUtils.getUserName(token, secretKey);
            User user = userService.getUserByUserName(userName);

            // 권한 부여
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e){
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getErrorMessage());
        }
        filterChain.doFilter(request, response);
    }
}
