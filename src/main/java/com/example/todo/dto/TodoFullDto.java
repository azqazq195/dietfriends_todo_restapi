package com.example.todo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.todo.entity.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoFullDto {
    private int id;
    private String name;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileInfoDto> fileInfos;
}
