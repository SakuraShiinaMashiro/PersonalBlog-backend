package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.OwnerProfileVO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 站点信息控制器，提供博主公开信息
 * @author czf
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class SiteController {
    private final UserService userService;

    /**
     * 获取博主头像与名称。
     *
     * @return 博主信息
     */
    @GetMapping("/owner-profile")
    public Result<OwnerProfileVO> ownerProfile() {
        BlogUser owner = userService.findOwner();
        return Result.success(new OwnerProfileVO(owner.getUsername(), owner.getAvatarUrl()));
    }
}
