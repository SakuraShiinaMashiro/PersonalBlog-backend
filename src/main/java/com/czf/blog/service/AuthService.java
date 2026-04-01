package com.czf.blog.service;

import com.czf.blog.dto.EmailVerifyDTO;
import com.czf.blog.dto.OwnerLoginDTO;
import com.czf.blog.dto.TokenPairVO;

/**
 * @description: 认证服务接口，处理博主登录与邮箱验证流程
 * @author czf
 * @date 2026-03-31
 */
public interface AuthService {
    /**
     * 校验博主账号密码并进入邮箱验证阶段。
     *
     * @param dto 博主登录参数
     */
    void ownerLogin(OwnerLoginDTO dto);

    /**
     * 校验邮箱验证码并签发 Token。
     *
     * @param dto 邮箱验证码参数
     * @return Token 对
     */
    TokenPairVO verifyEmailCode(EmailVerifyDTO dto);
}
