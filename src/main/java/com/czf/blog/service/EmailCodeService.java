package com.czf.blog.service;

/**
 * @description: 邮箱验证码服务接口
 * @author czf
 * @date 2026-03-31
 */
public interface EmailCodeService {
    /**
     * 生成并发送验证码。
     *
     * @param email 邮箱
     */
    void sendCode(String email);

    /**
     * 校验验证码有效性。
     *
     * @param email 邮箱
     * @param code 验证码
     */
    void verifyCode(String email, String code);
}
