package com.czf.blog.service;

import com.czf.blog.dto.AnimeImportResult;
import com.czf.blog.dto.BangumiDTOs;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AnimeService {
    /**
     * 调用 Bangumi API 搜索番剧信息
     *
     * @param keyword 搜索关键词
     * @return 匹配的番剧条目列表
     */
    List<BangumiDTOs.SubjectItem> searchBangumi(String keyword);

    /**
     * 从 Bangumi 导入番剧元数据并初始化本地追番进度
     *
     * @param bgmId     Bangumi 条目 ID
     * @param trackDate 开始追番时间（可空，空则默认当天）
     */
    AnimeImportResult importFromBangumi(int bgmId, LocalDate trackDate);

    /**
     * 根据条件筛选已追番剧，此处的年份和季度指代首播时间
     *
     * @param year           年份 (可选)
     * @param season         季度 (可选, 1-4)
     * @param status         追番状态 (可选, 0/1/2)
     * @param trackDateStart 开始追番起始日期 (可选)
     * @param trackDateEnd   开始追番结束日期 (可选)
     * @return 包含番剧元数据和进度信息的映射列表
     */
    List<Map<String, Object>> getAnimeListWithProgress(Integer year, Integer season, Integer status,
                                                       LocalDate trackDateStart, LocalDate trackDateEnd);

    /**
     * 切换特定集数的观看状态 (幂等 Toggle 逻辑)
     *
     * @param animeId      本地番剧 ID
     * @param episodeIndex 集数索引 (通常从 1 开始)
     */
    void toggleEpisode(Long animeId, Integer episodeIndex);

    /**
     * 将追番进度快速更新为“看到第 N 集”。
     *
     * @param animeId 本地番剧 ID
     * @param episode 目标集数（覆盖为 1..N）
     */
    void seenToEpisode(Long animeId, Integer episode);

    /**
     * 将追番进度快速更新为“一键看完”。
     *
     * @param animeId 本地番剧 ID
     */
    void completeAnime(Long animeId);

    /**
     * 将追番进度快速重置为空。
     *
     * @param animeId 本地番剧 ID
     */
    void resetProgress(Long animeId);

    /**
     * 更新指定番剧的开始追番时间。
     *
     * @param animeId   本地番剧 ID
     * @param trackDate 新的开始追番日期
     */
    void updateTrackDate(Long animeId, LocalDate trackDate);

    /**
     * 删除指定追番记录。
     *
     * @param animeId 本地番剧 ID
     */
    void deleteAnime(Long animeId);
}
