package com.example.todo.service;

import com.example.todo.config.JwtProvider;
import com.example.todo.dto.SignInRequestDto;
import com.example.todo.dto.SignUpRequestDto;
import com.example.todo.dto.TokenDto;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        if (userService.isExists(signUpRequestDto.getEmail())) {
            throw new ApiException(ErrorCode.EXISTS_EMAIL);
        }
        return userService.create(signUpRequestDto.toEntity()).toUserDto();
    }

    @Transactional
    public TokenDto signIn(SignInRequestDto signInRequestDto) {
        User user = userService.retrieve(signInRequestDto.getEmail());
        if (!user.getPassword().equals(signInRequestDto.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }
        // refresh token 저장 및 갱신은 구현하지 않음.
        return jwtProvider.createTokenDto(String.valueOf(user.getId()), user.getRoles());
    }
}
