package com.czf.blog.exception;

import com.czf.blog.exception.code.BizErrorCode;

/**
 * @description: 业务异常，用于承载可预期的业务错误信息与状态码
 * @author czf
 * @date 2026-03-21
 */
public class BizException extends RuntimeException {
    private final Integer code;

    /**
     * 使用默认错误码创建业务异常。
     *
     * @param message 业务异常描述
     */
    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 使用指定错误码创建业务异常。
     *
     * @param code 业务错误码
     * @param message 业务异常描述
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用业务错误码枚举创建业务异常。
     *
     * @param errorCode 业务错误码枚举
     */
    public BizException(BizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 获取业务错误码。
     *
     * @return 业务错误码
     */
    public Integer getCode() {
        return code;
    }
}
