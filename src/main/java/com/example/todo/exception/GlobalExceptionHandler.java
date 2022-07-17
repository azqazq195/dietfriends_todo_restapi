package com.example.todo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.todo.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // handled
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleCustomException(ApiException e) {
        log.info("api exception", e);
        return errorResponse(e.getErrorCode());
    }

    // parameter exception
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info("missing parameter exception", ex);
        return errorResponse(ErrorCode.MISSING_PARAMETER, ex.getParameterName());
    }

    // unhandled
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.info("unhandled exception", e);
        return errorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> errorResponse(ErrorCode errorCode, String... args) {
        if (args.length == 0) {
            return ResponseEntity.status(errorCode.getHttpStatus())
                    .body(ErrorResponse.builder()
                            .status(errorCode.getHttpStatus().name())
                            .message(errorCode.getMessage())
                            .build());
        }

        StringBuilder message = new StringBuilder();
        message.append(errorCode.getMessage());
        message.append(":");
        for (String arg : args) {
            message.append(" ").append(arg);
        }
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().name())
                        .message(message.toString())
                        .build());
    }
}
