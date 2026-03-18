package com.czf.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容列表展示 VO (不包含 Markdown 原文)
 * 
 * @author Gemini
 * @date 2026-03-18
 */
public record ContentListVO(
    Long id,
    String title,
    String summary,
    Integer moduleType,
    Integer status,
    String coverUrl,
    Integer views,
    LocalDateTime createTime,
    LocalDateTime updateTime,
    List<String> tags
) {}
