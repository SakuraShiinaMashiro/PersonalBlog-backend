package com.czf.blog.service.impl;

import com.czf.blog.dto.EmailVerifyDTO;
import com.czf.blog.dto.OwnerLoginDTO;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.enums.RoleTypeEnum;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.AuthService;
import com.czf.blog.service.EmailCodeService;
import com.czf.blog.service.TokenService;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: 认证服务实现，处理博主登录与邮箱校验
 * @author czf
 * @date 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailCodeService emailCodeService;
    private final TokenService tokenService;

    /**
     * 校验博主账号密码并进入邮箱验证阶段。
     *
     * @param dto 博主登录参数
     */
    @Override
    public void ownerLogin(OwnerLoginDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.username()) || !StringUtils.hasText(dto.password())) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        BlogUser user = userService.findByUsername(dto.username());
        if (user == null) {
            throw new BizException(BizErrorCode.AUTH_OWNER_NOT_FOUND);
        }
        if (!RoleTypeEnum.OWNER.name().equals(user.getRole())) {
            throw new BizException(BizErrorCode.AUTH_OWNER_NOT_FOUND);
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }

    /**
     * 校验邮箱验证码并签发 Token。
     *
     * @param dto 邮箱验证码参数
     * @return Token 对
     */
    @Override
    public TokenPairVO verifyEmailCode(EmailVerifyDTO dto) {
        BlogUser owner = userService.findOwner();
        if (owner.getEmail() == null || !owner.getEmail().equals(dto.email())) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_MISMATCH);
        }
        emailCodeService.verifyCode(dto.email(), dto.code());
        return tokenService.issueTokens(owner);
    }
}