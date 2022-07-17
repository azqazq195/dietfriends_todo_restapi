package com.example.todo.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive."),

    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "Missing parameter."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "Invalid value."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid password. check your password."),
    NOT_OWNER(HttpStatus.BAD_REQUEST, "Not owner."),
    EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "Exists email. Try another email."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token."),
    INVALID_AUTHORIZED(HttpStatus.UNAUTHORIZED, "Invalid Authorized."),
    INVALID_APIKEY(HttpStatus.UNAUTHORIZED, "Invalid Api Key."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not exists."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists."),

    FAILED_UPLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),;

    private final HttpStatus httpStatus;
    private final String message;
}
