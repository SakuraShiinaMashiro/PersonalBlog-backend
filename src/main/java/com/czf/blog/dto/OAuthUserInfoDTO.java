package com.czf.blog.dto;

/**
 * @description: OAuth 用户信息参数
 * @author czf
 * @date 2026-03-31
 */
public record OAuthUserInfoDTO(
        String provider,
        String providerUserId,
        String username,
        String avatarUrl) {
}
