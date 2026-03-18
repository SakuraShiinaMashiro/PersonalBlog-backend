package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章与标签关联表实体类
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("blog_content_tag")
public class BlogContentTag {
    private Long contentId;
    
    private Long tagId;
}
