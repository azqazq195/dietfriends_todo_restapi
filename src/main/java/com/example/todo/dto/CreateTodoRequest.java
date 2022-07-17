package com.example.todo.dto;

import com.example.todo.entity.Todo;
import com.example.todo.entity.User;

import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTodoRequest {
    private String name;
    private Boolean completed;

    public Todo toEntity(User user) {
        return Todo.builder()
                .name(name)
                .completed(completed)
                .user(user)
                .fileInfos(new ArrayList<>())
                .build();
    }

    public void validate() {
        if (
                ObjectUtils.isEmpty(name) ||
                ObjectUtils.isEmpty(completed)
        ) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }
    }
}
