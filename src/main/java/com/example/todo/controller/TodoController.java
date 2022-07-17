package com.example.todo.controller;

import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.response.Response;
import com.example.todo.service.TodoService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(
            @PathVariable int id
    ) {
        return Response.toResponseEntity(
                HttpStatus.OK,
                todoService.retrieve(id));
    }

    @GetMapping
    public ResponseEntity<Object> list(
            @RequestParam Integer limit,
            @RequestParam(required = false) Integer page
    ) {
        if (limit > 100) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }

        if (page == null) {
            page = 0;
        }

        return Response.toResponseEntity(
                HttpStatus.OK,
                todoService.list(PageRequest.of(page, limit)));
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestPart CreateTodoRequest request,
            @RequestPart(required = false) MultipartFile[] uploadFiles
    ) {
        request.validate();
        return Response.toResponseEntity(
                HttpStatus.OK,
                todoService.create(request, uploadFiles));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable int id,
            @RequestPart UpdateTodoRequest request,
            @RequestPart(required = false) MultipartFile[] uploadFiles
    ) {
        return Response.toResponseEntity(
                HttpStatus.OK,
                todoService.update(id, request, uploadFiles));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable int id
    ) {
        todoService.delete(id);
        return Response.toResponseEntity(HttpStatus.OK);
    }
}
