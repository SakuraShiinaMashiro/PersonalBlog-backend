package com.czf.blog.constant;

/**
 * @description: 认证相关常量
 * @author czf
 * @date 2026-04-01
 */
public class AuthConstants {
    private AuthConstants() {
    }

    /**
     * JWT Token 前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * JWT Token 前缀长度
     */
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
}