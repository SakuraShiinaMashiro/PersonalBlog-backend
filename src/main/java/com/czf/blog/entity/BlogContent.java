package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 内容主表实体类
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@Data
@TableName("blog_content")
public class BlogContent {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    
    private String summary;
    
    private String content;
    
    private Integer moduleType;
    
    private Integer status;
    
    private String coverUrl;
    
    private Integer views;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
