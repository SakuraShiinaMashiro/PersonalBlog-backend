package com.czf.blog.exception.code;

/**
 * @description: 业务错误码枚举，统一维护业务异常 code 与 message
 * @author czf
 * @date 2026-03-22
 */
public enum BizErrorCode {
    KEYWORD_EMPTY(40001, "搜索关键词不能为空"),
    BANGUMI_SEARCH_UNAVAILABLE(50301, "搜索服务暂时不可用，请稍后重试"),
    BANGUMI_SEARCH_TIMEOUT(50302, "搜索服务请求超时，请稍后重试"),
    AUTH_OWNER_NOT_FOUND(40101, "博主账号不存在"),
    AUTH_INVALID_CREDENTIALS(40102, "账号或密码错误"),
    AUTH_EMAIL_MISMATCH(40103, "邮箱不匹配"),
    AUTH_EMAIL_CODE_INVALID(40104, "验证码错误"),
    AUTH_EMAIL_CODE_EXPIRED(40105, "验证码已过期"),
    AUTH_EMAIL_CODE_COOLDOWN(40106, "验证码发送过于频繁"),
    AUTH_OWNER_KEY_INVALID(40107, "密钥校验失败"),
    AUTH_REFRESH_INVALID(40108, "Refresh Token 无效"),
    AUTH_ACCESS_DENIED(40301, "没有权限访问该资源"),
    AUTH_OAUTH_NOT_SUPPORTED(50101, "第三方登录暂未配置");

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
