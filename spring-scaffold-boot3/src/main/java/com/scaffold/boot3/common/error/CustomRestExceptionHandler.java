package com.scaffold.boot3.common.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        e.printStackTrace();
        ApiError apiError =
            new ApiError(
                HttpStatus.FORBIDDEN,
                "권한이 없습니다",
                "FORBIDDEN");

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception e) {
        ApiError apiError =
            new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getLocalizedMessage(),
                "error occurred");

        e.printStackTrace();

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
