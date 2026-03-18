package com.czf.blog.dto;

import java.util.List;

/**
 * 内容保存请求参数 DTO
 * 
 * @author Gemini
 * @date 2026-03-18
 */
public record ContentSaveDTO(
    Long id,
    String title,
    String summary,
    String content,
    Integer moduleType,
    Integer status,
    String coverUrl,
    List<String> tags
) {}
