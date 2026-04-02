package com.czf.blog.dto;

/**
 * @description: 邮箱验证状态返回模型
 * @author czf
 * @date 2026-03-31
 */
public record EmailVerifyStatusVO(boolean needEmailVerify, String email) {
}
