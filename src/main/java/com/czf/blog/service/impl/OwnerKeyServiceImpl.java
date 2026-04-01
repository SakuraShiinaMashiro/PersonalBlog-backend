package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.utils.HashUtils;
import com.czf.blog.entity.BlogOwnerKey;
import com.czf.blog.mapper.BlogOwnerKeyMapper;
import com.czf.blog.service.OwnerKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @description: 博主登录入口密钥服务实现
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class OwnerKeyServiceImpl implements OwnerKeyService {
    private final BlogOwnerKeyMapper ownerKeyMapper;

    /**
     * 校验密钥内容是否有效。
     *
     * @param content 密钥文件内容
     * @return true 表示校验成功
     */
    @Override
    public boolean verifyKey(byte[] content) {
        String hash = HashUtils.sha256(content);
        BlogOwnerKey key = ownerKeyMapper.selectOne(new LambdaQueryWrapper<BlogOwnerKey>()
                .eq(BlogOwnerKey::getKeyHash, hash)
                .eq(BlogOwnerKey::getEnabled, 1)
                .last("limit 1"));
        return key != null;
    }
}
