package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 笔记与标签关联表实体类
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("blog_note_tag")
public class BlogNoteTag {
    @TableField("note_id")
    private Long noteId;
    @TableField("tag_id")
    private Long tagId;
}
