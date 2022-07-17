package com.example.todo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.todo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final AntPathRequestMatcher[] excludedMatchers = {
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher(
                    "/todos/**"
                    ,HttpMethod.GET.toString()
            ),
            new AntPathRequestMatcher(
                    "/todos**"
                    ,HttpMethod.GET.toString()
            ),
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = environment.getProperty("config.apikey");
        String key = request.getParameter("apikey");
        if (!StringUtils.hasText(key) || !Objects.equals(apiKey, key)) {
            SecurityConfiguration.error(response, ErrorCode.INVALID_APIKEY);
            return;
        }

        filterChain.doFilter(request, response);
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(excludedMatchers)
                .anyMatch(matcher -> matcher.matches(request));
    }
}
