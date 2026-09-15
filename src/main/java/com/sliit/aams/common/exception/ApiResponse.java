package com.sliit.aams.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data)        { return new ApiResponse<>(true, "OK", data); }
    public static <T> ApiResponse<T> ok(String m, T d) { return new ApiResponse<>(true, m, d); }
    public static <T> ApiResponse<T> fail(String m)    { return new ApiResponse<>(false, m, null); }
}