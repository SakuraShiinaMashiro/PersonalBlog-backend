package com.czf.blog.dto;

import java.util.List;

/**
 * 笔记保存请求参数 DTO
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
public record NoteSaveDTO(
    Long id,
    String title,
    String summary,
    String content,
    Integer moduleType,
    Integer status,
    String coverUrl,
    List<String> tags
) {}
