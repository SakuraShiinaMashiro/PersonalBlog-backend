package com.czf.blog.service.impl;

import com.czf.blog.dto.OwnerLoginDTO;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.enums.RoleTypeEnum;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.AuthService;
import com.czf.blog.service.TokenService;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: 认证服务实现，处理博主登录
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * 校验博主账号密码并签发 Token。
     *
     * @param dto 博主登录参数
     * @return Token 对
     */
    @Override
    public TokenPairVO ownerLogin(OwnerLoginDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.username()) || !StringUtils.hasText(dto.password())) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        BlogUser user = userService.findByUsername(dto.username());
        if (user == null) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        if (!RoleTypeEnum.OWNER.name().equals(user.getRole())) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new BizException(BizErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        return tokenService.issueTokens(user);
    }
}
