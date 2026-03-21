package com.czf.blog.dto;

/**
 * 切换单集观看状态请求参数。
 *
 * @author czf
 * @date 2026-03-21
 */
public record AnimeToggleEpisodeDTO(
        Long animeId,
        Integer episodeIndex
) {
}
