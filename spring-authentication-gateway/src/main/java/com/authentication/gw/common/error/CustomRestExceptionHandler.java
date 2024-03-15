package com.authentication.gw.common.error;

import com.authentication.gw.common.model.ApiResponse;
import com.authentication.gw.common.model.ApiStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseStatusExceptionHandler {

    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<?>> handleMethodNotAllowedException(MethodNotAllowedException ex) {

        ApiResponse<?> res = new ApiResponse<>(ApiStatus.BAD_REQUEST, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public Mono<ResponseEntity<?>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {

        ApiResponse<?> res = new ApiResponse<>(ApiStatus.UNAUTHORIZED);
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<?>> handleAccessDeniedExceptiondException(AccessDeniedException ex) {

        ApiResponse<?> res = new ApiResponse<>(ApiStatus.UNAUTHORIZED);
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }


    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<?>> handleNotValidException(WebExchangeBindException ex) {

        ApiResponse<?> res = new ApiResponse<>(ApiStatus.BAD_REQUEST_NOT_VALID);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }


    @ExceptionHandler(ApiException.class)
    public Mono<ResponseEntity<?>> handleCustomException(ApiException ex) {
        ApiResponse<?> res = new ApiResponse<>(ex.getCode(), ex.getMessage());
        return Mono.just(ResponseEntity.status(ex.getStatus())
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }


    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<?>>> handleException(Exception ex) {
        ex.printStackTrace();
        ApiResponse<String> res = new ApiResponse<>(ApiStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(res));
    }

}
