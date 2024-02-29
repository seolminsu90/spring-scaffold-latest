package com.authentication.gw.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String msg;
    private T data;

    public ApiResponse(int status) {
        this.status = status;
    }

    public ApiResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiResponse(ApiStatus status) {
        this.status = status.getCode();
        this.msg = status.getDesc();
    }

    public ApiResponse(ApiStatus status, T data) {
        this.status = status.getCode();
        this.msg = status.getDesc();
        this.data = data;
    }

    public ApiResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(int status, T data, String msg) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
