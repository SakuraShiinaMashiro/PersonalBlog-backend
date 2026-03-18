package com.czf.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czf.blog.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客标签 Mapper 接口
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {
}
