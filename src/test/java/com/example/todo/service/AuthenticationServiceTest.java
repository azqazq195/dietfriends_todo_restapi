package com.example.todo.service;

import com.example.todo.dto.SignInRequestDto;
import com.example.todo.dto.SignUpRequestDto;
import com.example.todo.dto.TokenDto;
import com.example.todo.exception.ApiException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local")
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;

    private SignUpRequestDto signUpRequestDto;
    private SignInRequestDto signInRequestDto;

    @BeforeAll
    void setUp() {
        signUpRequestDto = new SignUpRequestDto(
                "seongha",
                "mail@mail.com",
                "qwe123",
                1
        );
        signInRequestDto = new SignInRequestDto(
                "mail@mail.com",
                "qwe123"
        );
    }

    @Test
    void signUp() {
        authenticationService.signUp(signUpRequestDto);
        assertTrue(userService.retrieve(signInRequestDto.getEmail()).getId() > 0);
        assertThrows(ApiException.class, () -> authenticationService.signUp(signUpRequestDto));
    }

    @Test
    void signIn() {
        authenticationService.signUp(signUpRequestDto);
        TokenDto tokenDto = authenticationService.signIn(signInRequestDto);
        assertNotNull(tokenDto);
        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());

        signInRequestDto.setPassword("wrong");
        assertThrows(ApiException.class, () -> authenticationService.signUp(signUpRequestDto));
    }
}