package com.example.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.entity.FileInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoFullDto;
import com.example.todo.dto.TodoPartialDto;
import com.example.todo.entity.Todo;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import com.example.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;
    private final FileInfoService fileInfoService;

    @Transactional
    public TodoFullDto create(CreateTodoRequest createTodoRequest, MultipartFile[] multipartFiles) {
        Todo todo = createTodoRequest.toEntity(userService.requestedUser());
        for (FileInfo fileInfo : fileInfoService.multipartsToFileInfos(multipartFiles)) {
            todo.addFile(fileInfo);
        }
        return todoRepository.save(todo).toTodoFullDto();
    }

    @Transactional
    public TodoFullDto update(int id, UpdateTodoRequest updateTodoRequest, MultipartFile[] multipartFiles) {
        checkExists(id);
        Todo todo = todoRepository.getById(id);
        checkOwner(todo, userService.requestedUserId());
        todo.updateValues(updateTodoRequest);
        todo.updateFileInfos(fileInfoService.findAllById(updateTodoRequest.getFileInfoIds()));
        for (FileInfo fileInfo : fileInfoService.multipartsToFileInfos(multipartFiles)) {
            todo.addFile(fileInfo);
        }
        return todoRepository.save(todo).toTodoFullDto();
    }

    @Transactional
    public void delete(int id) {
        checkExists(id);
        checkOwner(todoRepository.getById(id), userService.requestedUserId());
        todoRepository.deleteById(id);
    }

    @Transactional
    public TodoFullDto retrieve(int id) {
        checkExists(id);
        Todo todo = todoRepository.getById(id);
        return todo.toTodoFullDto();
    }

    @Transactional
    public List<TodoPartialDto> list(Pageable pageable) {
        List<Todo> todoList = todoRepository.findAll(pageable).getContent();
        return todoList.stream().map(Todo::toTodoPartialDto).collect(Collectors.toList());
    }

    @Transactional
    public void checkExists(int id) {
        if (!todoRepository.existsById(id)) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Transactional
    public void checkOwner(Todo todo, int userId) {
        if (todo.getUser().getId() != userId) {
            throw new ApiException(ErrorCode.NOT_OWNER);
        }
    }
}
