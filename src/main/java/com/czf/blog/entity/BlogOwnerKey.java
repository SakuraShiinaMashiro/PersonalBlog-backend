package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 博主登录入口密钥实体，仅用于前端解锁
 * @author czf
 * @date 2026-03-31
 */
@Data
@TableName("blog_owner_key")
public class BlogOwnerKey {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String keyHash;

    private Integer enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
