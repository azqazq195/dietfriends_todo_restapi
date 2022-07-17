package com.example.todo.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = jwtProvider.resolveToken(req);

        String method = req.getMethod();
        String requestUri = req.getRequestURI().toString();
        log.info("request: [" + method + "] - " + requestUri);
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            try {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ApiException e) {
                SecurityConfiguration.error(res, e.getErrorCode());
            }
        } else {
            SecurityConfiguration.error(res, ErrorCode.INVALID_TOKEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
