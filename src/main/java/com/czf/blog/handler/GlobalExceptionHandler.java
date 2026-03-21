package com.czf.blog.handler;

import com.czf.blog.common.Result;
import com.czf.blog.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @description: 全局异常处理器，统一将异常转换为标准 Result 响应
 * @author czf
 * @date 2026-03-21
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常并返回可读的业务错误信息。
     *
     * @param e 业务异常对象
     * @return 统一返回结构，包含业务错误码与错误信息
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理未知异常并返回通用错误信息。
     *
     * @param e 未知异常对象
     * @return 统一返回结构，避免向前端暴露内部实现细节
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return Result.error("系统异常，请稍后重试");
    }
}
