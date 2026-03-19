package com.czf.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔记列表展示 VO (不包含 Markdown 原文)
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
public record NoteListVO(
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
