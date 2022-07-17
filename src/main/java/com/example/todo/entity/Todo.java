package com.example.todo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.example.todo.dto.TodoFullDto;
import com.example.todo.dto.TodoPartialDto;

import com.example.todo.dto.UpdateTodoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "todo")
public class Todo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Boolean completed;
    private LocalDateTime completedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileInfo> fileInfos;

    public void addFile(FileInfo fileInfo) {
        if (fileInfos == null) {
            fileInfos = new ArrayList<>();
        }

        fileInfos.add(fileInfo);
        fileInfo.setTodo(this);
    }

    public void removeFile(FileInfo fileInfo) {
        fileInfos.remove(fileInfo);
        fileInfo.setTodo(null);
    }

    public void updateValues(UpdateTodoRequest createTodoRequest) {
        if (createTodoRequest.getName() != null) {
            this.name = createTodoRequest.getName();
        }
        if (createTodoRequest.getCompleted() != null) {
            this.completed = createTodoRequest.getCompleted();
        }
    }

    public void updateFileInfos(List<FileInfo> fileInfos) {
        if (fileInfos != null) {
            this.fileInfos = fileInfos;
        }
    }

    public TodoFullDto toTodoFullDto() {
        return new TodoFullDto(
                id,
                name,
                completed,
                completedAt,
                super.getCreatedAt(),
                super.getUpdatedAt(),
                fileInfos.stream().map(FileInfo::toFileInfoDto).collect(Collectors.toList())
        );
    }

    public TodoPartialDto toTodoPartialDto() {
        return new TodoPartialDto(
                id,
                name,
                completed);
    }
}
