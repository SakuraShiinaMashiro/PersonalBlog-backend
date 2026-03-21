package com.czf.blog.dto;

import java.time.LocalDate;

/**
 * 导入番剧请求参数。
 *
 * @author czf
 * @date 2026-03-21
 */
public record AnimeImportDTO(
        Integer bgmId,
        LocalDate trackDate
) {
}
