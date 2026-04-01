package com.czf.blog.service.impl;

import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @description: OAuth 登录服务实现
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    /**
     * 获取第三方登录授权地址。
     *
     * @param provider 平台标识
     * @return 授权跳转地址
     */
    @Override
    public String getAuthorizeUrl(String provider) {
        throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
    }

    /**
     * 处理 OAuth 回调并签发 Token。
     *
     * @param provider 平台标识
     * @param code 授权码
     * @param state 状态码
     * @return Token 对
     */
    @Override
    public TokenPairVO handleCallback(String provider, String code, String state) {
        throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
    }
}
