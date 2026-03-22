package com.czf.blog.exception.code;

/**
 * @description: 业务错误码枚举，统一维护业务异常 code 与 message
 * @author czf
 * @date 2026-03-22
 */
public enum BizErrorCode {
    KEYWORD_EMPTY(40001, "搜索关键词不能为空"),
    BANGUMI_SEARCH_UNAVAILABLE(50301, "搜索服务暂时不可用，请稍后重试"),
    BANGUMI_SEARCH_TIMEOUT(50302, "搜索服务请求超时，请稍后重试");

    private final Integer code;
    private final String message;

    BizErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取业务错误码。
     *
     * @return 业务错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取业务错误信息。
     *
     * @return 业务错误信息
     */
    public String getMessage() {
        return message;
    }
}
