package com.czf.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容详情展示 VO (包含全文)
 * 
 * @author Gemini
 * @date 2026-03-18
 */
public record ContentDetailVO(
    Long id,
    String title,
    String content,
    Integer moduleType,
    Integer status,
    String coverUrl,
    Integer views,
    LocalDateTime createTime,
    LocalDateTime updateTime,
    List<String> tags
) {}
