package com.czf.blog.dto;

/**
 * @description: 邮箱验证码校验参数
 * @author czf
 * @date 2026-03-31
 */
public record EmailVerifyDTO(String email, String code) {
}
