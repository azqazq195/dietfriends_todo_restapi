package com.example.todo.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    public static ResponseEntity<Object> toResponseEntity(HttpStatus status) {
        return ResponseEntity.status(status).build();
    }

    public static ResponseEntity<Object> toResponseEntity(HttpStatus status, Object object) {
        return ResponseEntity.status(status).body(object);
    }
}
