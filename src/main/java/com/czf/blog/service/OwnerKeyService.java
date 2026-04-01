package com.czf.blog.service;

/**
 * @description: 博主登录入口密钥服务接口
 * @author czf
 * @date 2026-03-31
 */
public interface OwnerKeyService {
    /**
     * 校验密钥内容是否有效。
     *
     * @param content 密钥文件内容
     * @return true 表示校验成功
     */
    boolean verifyKey(byte[] content);
}
