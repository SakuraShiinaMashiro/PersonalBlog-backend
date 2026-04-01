package com.czf.blog.service;

import com.czf.blog.dto.OAuthUserInfoDTO;
import com.czf.blog.entity.BlogUser;

/**
 * @description: 用户服务接口
 * @author czf
 * @date 2026-03-31
 */
public interface UserService {
    /**
     * 获取博主用户。
     *
     * @return 博主用户
     */
    BlogUser findOwner();

    /**
     * 按用户名查询用户。
     *
     * @param username 用户名
     * @return 用户信息，未找到返回 null
     */
    BlogUser findByUsername(String username);

    /**
     * OAuth 登录时创建或绑定游客账号。
     *
     * @param info OAuth 用户信息
     * @return 游客用户
     */
    BlogUser findOrCreateVisitor(OAuthUserInfoDTO info);
}
