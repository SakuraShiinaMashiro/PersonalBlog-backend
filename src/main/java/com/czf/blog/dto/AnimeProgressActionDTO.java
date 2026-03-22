package com.czf.blog.dto;

/**
 * 仅携带番剧ID的进度操作请求参数。
 *
 * @author czf
 * @date 2026-03-21
 */
public record AnimeProgressActionDTO(
        Long animeId
) {
}
