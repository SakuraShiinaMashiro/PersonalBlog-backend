package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 邮箱验证码实体，用于博主登录二次校验
 * @author czf
 * @date 2026-03-31
 */
@Data
@TableName("blog_email_code")
public class BlogEmailCode {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String email;

    private String codeHash;

    private LocalDateTime expireAt;

    private Integer used;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
