package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: Refresh Token 实体，用于刷新 Access Token 与登出失效
 * @author czf
 * @date 2026-03-31
 */
@Data
@TableName("blog_refresh_token")
public class BlogRefreshToken {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String tokenHash;

    private LocalDateTime expireAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
