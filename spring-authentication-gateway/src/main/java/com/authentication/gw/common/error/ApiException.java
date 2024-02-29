package com.authentication.gw.common.error;

import com.authentication.gw.common.model.ApiStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final int code;

    public ApiException(HttpStatus status, ApiStatus apiStatus) {
        super(apiStatus.getDesc());
        this.status = status;
        this.code = apiStatus.getCode();
    }

    public ApiException(HttpStatus status, String message, int code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.code = status.value();
    }
}
