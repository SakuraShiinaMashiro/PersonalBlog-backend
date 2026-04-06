package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.enums.RoleTypeEnum;
import com.czf.blog.dto.OAuthUserInfoDTO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.entity.BlogUserOauth;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.mapper.BlogUserMapper;
import com.czf.blog.mapper.BlogUserOauthMapper;
import com.czf.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: 用户服务实现
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final BlogUserMapper userMapper;
    private final BlogUserOauthMapper oauthMapper;

    /**
     * 获取博主用户。
     *
     * @return 博主用户
     */
    @Override
    public BlogUser findOwner() {
        BlogUser owner = userMapper.selectOne(new LambdaQueryWrapper<BlogUser>()
                .eq(BlogUser::getRole, RoleTypeEnum.OWNER.name())
                .last("limit 1"));
        if (owner == null) {
            throw new BizException(BizErrorCode.AUTH_OWNER_NOT_FOUND);
        }
        return owner;
    }

    /**
     * 按用户名查询用户。
     *
     * @param username 用户名
     * @return 用户信息，未找到返回 null
     */
    @Override
    public BlogUser findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<BlogUser>()
                .eq(BlogUser::getUsername, username)
                .last("limit 1"));
    }

    /**
     * OAuth 登录时创建或绑定游客账号。
     *
     * @param info OAuth 用户信息
     * @return 游客用户
     */
    @Override
    public BlogUser findOrCreateVisitor(OAuthUserInfoDTO info) {
        BlogUserOauth binding = oauthMapper.selectOne(new LambdaQueryWrapper<BlogUserOauth>()
                .eq(BlogUserOauth::getProvider, info.provider())
                .eq(BlogUserOauth::getProviderUserId, info.providerUserId())
                .last("limit 1"));
        if (binding != null) {
            return userMapper.selectById(binding.getUserId());
        }

        BlogUser user = new BlogUser();
        user.setRole(RoleTypeEnum.VISITOR.name());
        user.setUsername(StringUtils.hasText(info.username()) ? info.username() : "visitor_" + info.providerUserId());
        user.setAvatarUrl(info.avatarUrl());
        user.setStatus(1);
        userMapper.insert(user);

        BlogUserOauth oauth = new BlogUserOauth();
        oauth.setUserId(user.getId());
        oauth.setProvider(info.provider());
        oauth.setProviderUserId(info.providerUserId());
        oauthMapper.insert(oauth);
        return user;
    }
}
