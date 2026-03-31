package com.czf.blog.common;

import com.czf.blog.exception.BizException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: 哈希工具类，用于生成 SHA-256 摘要
 * @author czf
 * @date 2026-03-31
 */
public class HashUtils {
    private HashUtils() {
    }

    /**
     * 计算字符串的 SHA-256 哈希值。
     *
     * @param content 待计算内容
     * @return 哈希后的十六进制字符串
     */
    public static String sha256(String content) {
        return sha256(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算字节数组的 SHA-256 哈希值。
     *
     * @param content 待计算内容
     * @return 哈希后的十六进制字符串
     */
    public static String sha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(content);
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("SHA-256 算法不可用");
        }
    }
}
