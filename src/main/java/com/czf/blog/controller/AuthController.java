package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.EmailCodeDTO;
import com.czf.blog.dto.EmailSendVO;
import com.czf.blog.dto.EmailVerifyDTO;
import com.czf.blog.dto.EmailVerifyStatusVO;
import com.czf.blog.dto.LogoutDTO;
import com.czf.blog.dto.OwnerKeyVerifyVO;
import com.czf.blog.dto.OwnerLoginDTO;
import com.czf.blog.dto.RefreshTokenDTO;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.AuthService;
import com.czf.blog.service.EmailCodeService;
import com.czf.blog.service.OwnerKeyService;
import com.czf.blog.service.TokenService;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @description: 认证控制器，提供登录与验证接口
 * @author czf
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final OwnerKeyService ownerKeyService;
    private final AuthService authService;
    private final EmailCodeService emailCodeService;
    private final TokenService tokenService;
    private final UserService userService;

    /**
     * 校验博主登录入口密钥。
     *
     * @param file 密钥文件
     * @return 校验结果
     */
    @PostMapping("/owner/key-verify")
    public Result<OwnerKeyVerifyVO> verifyOwnerKey(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(BizErrorCode.AUTH_OWNER_KEY_INVALID);
        }
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new BizException(BizErrorCode.AUTH_OWNER_KEY_INVALID);
        }
        boolean unlocked = ownerKeyService.verifyKey(content);
        if (!unlocked) {
            throw new BizException(BizErrorCode.AUTH_OWNER_KEY_INVALID);
        }
        return Result.success(new OwnerKeyVerifyVO(true));
    }

    /**
     * 博主账号密码登录。
     *
     * @param dto 登录参数
     * @return 是否需要邮箱验证与博主邮箱
     */
    @PostMapping("/owner/login")
    public Result<EmailVerifyStatusVO> ownerLogin(@RequestBody OwnerLoginDTO dto) {
        String email = authService.ownerLogin(dto);
        return Result.success(new EmailVerifyStatusVO(true, email));
    }

    /**
     * 发送邮箱验证码。
     *
     * @param dto 邮箱参数
     * @return 发送结果
     */
    @PostMapping("/owner/send-email-code")
    public Result<EmailSendVO> sendEmailCode(@RequestBody EmailCodeDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.email())) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_MISMATCH);
        }
        String ownerEmail = userService.findOwner().getEmail();
        if (!StringUtils.hasText(ownerEmail) || !ownerEmail.equals(dto.email())) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_MISMATCH);
        }
        emailCodeService.sendCode(dto.email());
        return Result.success(new EmailSendVO(true));
    }

    /**
     * 校验邮箱验证码并签发 Token。
     *
     * @param dto 邮箱验证码参数
     * @return Token 对
     */
    @PostMapping("/owner/email-verify")
    public Result<TokenPairVO> verifyEmailCode(@RequestBody EmailVerifyDTO dto) {
        return Result.success(authService.verifyEmailCode(dto));
    }

    /**
     * 刷新 Token。
     *
     * @param dto 刷新参数
     * @return Token 对
     */
    @PostMapping("/refresh")
    public Result<TokenPairVO> refresh(@RequestBody RefreshTokenDTO dto) {
        return Result.success(tokenService.refresh(dto.refreshToken()));
    }

    /**
     * 登出并使 Refresh Token 失效。
     *
     * @param dto 登出参数
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody LogoutDTO dto) {
        tokenService.invalidate(dto.refreshToken());
        return Result.success();
    }
}
