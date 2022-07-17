package com.example.todo.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTodoRequest {
    private String name;
    private Boolean completed;
    private List<Integer> fileInfoIds;
}
