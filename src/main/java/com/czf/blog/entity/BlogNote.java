package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 笔记主表实体类
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
@Data
@TableName("blog_note")
public class BlogNote {
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
