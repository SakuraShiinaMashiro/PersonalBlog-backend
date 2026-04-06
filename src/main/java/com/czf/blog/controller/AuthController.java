package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.LogoutDTO;
import com.czf.blog.dto.OwnerKeyVerifyVO;
import com.czf.blog.dto.OwnerLoginDTO;
import com.czf.blog.dto.RefreshTokenDTO;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.AuthService;
import com.czf.blog.service.OwnerKeyService;
import com.czf.blog.service.TokenService;
import lombok.RequiredArgsConstructor;
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
    private final TokenService tokenService;

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
     * @return Token 对
     */
    @PostMapping("/owner/login")
    public Result<TokenPairVO> ownerLogin(@RequestBody OwnerLoginDTO dto) {
        return Result.success(authService.ownerLogin(dto));
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
