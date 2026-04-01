package com.czf.blog.service;

import com.czf.blog.dto.TokenPairVO;

/**
 * @description: OAuth 登录服务接口
 * @author czf
 * @date 2026-03-31
 */
public interface OAuthService {
    /**
     * 获取第三方登录授权地址。
     *
     * @param provider 平台标识
     * @return 授权跳转地址
     */
    String getAuthorizeUrl(String provider);

    /**
     * 处理 OAuth 回调并签发 Token。
     *
     * @param provider 平台标识
     * @param code 授权码
     * @param state 状态码
     * @return Token 对
     */
    TokenPairVO handleCallback(String provider, String code, String state);
}
