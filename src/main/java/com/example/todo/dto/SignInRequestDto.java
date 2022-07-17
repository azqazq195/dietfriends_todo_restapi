package com.example.todo.dto;

import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {
    private String email;
    private String password;

    public void validate() {
        if (
                ObjectUtils.isEmpty(email) ||
                ObjectUtils.isEmpty(password)
        ) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }
    }
}
