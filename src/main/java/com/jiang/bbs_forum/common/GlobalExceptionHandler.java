package com.jiang.bbs_forum.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Response.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数校验失败");
        log.warn("参数校验失败: {}", msg);
        return Response.error(400, msg);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Response<Void> handleUploadSize(MaxUploadSizeExceededException e) {
        return Response.error(400, "上传文件大小超过限制");
    }

    @ExceptionHandler(DataAccessException.class)
    public Response<Void> handleDataAccess(DataAccessException e) {
        log.error("数据库异常", e);
        return Response.error(500, "服务器内部错误");
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        log.error("未捕获异常", e);
        return Response.error(500, "服务器内部错误");
    }
}
