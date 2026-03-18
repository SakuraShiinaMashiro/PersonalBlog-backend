package com.czf.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czf.blog.entity.BlogContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客内容 Mapper 接口
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@Mapper
public interface BlogContentMapper extends BaseMapper<BlogContent> {
}
