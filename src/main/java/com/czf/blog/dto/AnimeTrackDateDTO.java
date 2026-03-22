package com.czf.blog.dto;

import java.time.LocalDate;

/**
 * 更新开始追番时间请求参数。
 *
 * @author czf
 * @date 2026-03-21
 */
public record AnimeTrackDateDTO(
        Long animeId,
        LocalDate trackDate
) {
}
