package com.example.todo.dto;

import com.example.todo.entity.User;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String name;
    private String email;
    private String password;
    private Integer age;

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .roles(Collections.singletonList("ROLE_USER"))
                .age(age)
                .build();
    }

    public void validate() {
        if (
               ObjectUtils.isEmpty(name) ||
               ObjectUtils.isEmpty(email) ||
               ObjectUtils.isEmpty(password)
        ) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }

        if (age < 0 || age > 150) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }
    }
}
