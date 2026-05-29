package com.jiang.bbs_forum.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> created(String message, T data) {
        return new Response<>(201, message, data);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }
}
