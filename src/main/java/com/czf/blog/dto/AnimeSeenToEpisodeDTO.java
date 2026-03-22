package com.czf.blog.dto;

/**
 * 快捷设置看到第N集请求参数。
 *
 * @author czf
 * @date 2026-03-21
 */
public record AnimeSeenToEpisodeDTO(
        Long animeId,
        Integer episode
) {
}
