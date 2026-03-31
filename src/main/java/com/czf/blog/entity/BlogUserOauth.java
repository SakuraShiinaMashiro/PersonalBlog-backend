package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: OAuth 绑定实体，用于记录游客账号与第三方平台的关联关系
 * @author czf
 * @date 2026-03-31
 */
@Data
@TableName("blog_user_oauth")
public class BlogUserOauth {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String provider;

    private String providerUserId;

    private String accessToken;

    private String refreshToken;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
