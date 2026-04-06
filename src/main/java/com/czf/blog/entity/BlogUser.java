package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 用户主表实体，用于存储博主与游客的基础信息
 * @author czf
 * @date 2026-03-31
 */
@Data
@TableName("blog_user")
public class BlogUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String role;

    private String username;

    private String passwordHash;

    private String email;

    private String avatarUrl;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
