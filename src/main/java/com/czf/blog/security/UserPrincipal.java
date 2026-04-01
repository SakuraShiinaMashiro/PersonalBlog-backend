package com.czf.blog.security;

/**
 * @description: 当前登录用户信息模型
 * @author czf
 * @date 2026-03-31
 */
public record UserPrincipal(Long id, String username, String role) {
}
