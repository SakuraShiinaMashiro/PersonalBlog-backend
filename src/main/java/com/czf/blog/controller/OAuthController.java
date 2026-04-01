package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description: OAuth 控制器，处理第三方登录跳转与回调
 * @author czf
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    /**
     * 获取第三方授权跳转地址。
     *
     * @param provider 平台标识
     * @return 跳转响应
     */
    @GetMapping("/{provider}/authorize")
    public ResponseEntity<Void> authorize(@PathVariable("provider") String provider) {
        String url = oAuthService.getAuthorizeUrl(provider);
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, url).build();
    }

    /**
     * OAuth 回调处理。
     *
     * @param provider 平台标识
     * @param code 授权码
     * @param state 状态码
     * @return Token 对
     */
    @GetMapping("/{provider}/callback")
    public Result<TokenPairVO> callback(@PathVariable("provider") String provider,
                                        @RequestParam("code") String code,
                                        @RequestParam(value = "state", required = false) String state) {
        return Result.success(oAuthService.handleCallback(provider, code, state));
    }
}
