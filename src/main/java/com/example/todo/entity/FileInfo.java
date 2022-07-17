package com.example.todo.entity;

import com.example.todo.dto.FileInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "file_info")
public class FileInfo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private long size;
    private String path;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public FileInfoDto toFileInfoDto() {
        return new FileInfoDto(
                id,
                name,
                size,
                path
        );
    }
}
