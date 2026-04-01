package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.UserProfileVO;
import com.czf.blog.exception.BizException;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.security.UserPrincipal;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户控制器，提供当前登录用户信息
 * @author czf
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    /**
     * 获取当前登录用户信息。
     *
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserProfileVO> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BizException(401, "未登录或登录已过期");
        }
        BlogUser user = userService.findByUsername(principal.username());
        String avatarUrl = user == null ? null : user.getAvatarUrl();
        return Result.success(new UserProfileVO(principal.id(), principal.role(), principal.username(), avatarUrl));
    }
}
