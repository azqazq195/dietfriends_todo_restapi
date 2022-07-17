package com.example.todo.controller;

import com.example.todo.dto.SignInRequestDto;
import com.example.todo.dto.SignUpRequestDto;
import com.example.todo.response.Response;
import com.example.todo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(
            @RequestBody SignUpRequestDto signUpRequestDto
    ) {
        signUpRequestDto.validate();
        return Response.toResponseEntity(
                HttpStatus.OK,
                authenticationService.signUp(signUpRequestDto)
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(
            @RequestBody SignInRequestDto signInRequestDto
            ) {
        signInRequestDto.validate();
        return Response.toResponseEntity(
                HttpStatus.OK,
                authenticationService.signIn(signInRequestDto)
        );
    }
}
