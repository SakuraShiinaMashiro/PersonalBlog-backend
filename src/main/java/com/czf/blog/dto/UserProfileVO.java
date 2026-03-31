package com.czf.blog.dto;

/**
 * @description: 当前登录用户信息返回模型
 * @author czf
 * @date 2026-03-31
 */
public record UserProfileVO(Long id, String role, String username, String avatarUrl) {
}
