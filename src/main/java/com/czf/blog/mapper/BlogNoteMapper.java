package com.czf.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czf.blog.entity.BlogNote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客笔记 Mapper 接口
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
@Mapper
public interface BlogNoteMapper extends BaseMapper<BlogNote> {
}
