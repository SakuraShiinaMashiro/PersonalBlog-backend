package com.czf.blog.service;

import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.security.UserPrincipal;

/**
 * @description: Token 服务接口，负责签发与解析 JWT
 * @author czf
 * @date 2026-03-31
 */
public interface TokenService {
    /**
     * 签发 Access/Refresh Token。
     *
     * @param user 用户信息
     * @return Token 对
     */
    TokenPairVO issueTokens(BlogUser user);

    /**
     * 刷新 Access Token。
     *
     * @param refreshToken Refresh Token
     * @return Token 对
     */
    TokenPairVO refresh(String refreshToken);

    /**
     * 使 Refresh Token 失效。
     *
     * @param refreshToken Refresh Token
     */
    void invalidate(String refreshToken);

    /**
     * 解析 Access Token 获取用户信息。
     *
     * @param token Access Token
     * @return 用户信息模型，解析失败返回 null
     */
    UserPrincipal parseAccessToken(String token);
}
