package com.czf.blog.service;

import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.entity.AnimeSubject;
import com.czf.blog.entity.AnimeProgress;
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
     * @param airYear   放送年份
     * @param airSeason 放送季度 (1-4)
     */
    void importFromBangumi(int bgmId, int airYear, int airSeason);

    /**
     * 获取指定季度下的本地番剧列表及其追番进度
     *
     * @param year   年份 (可选)
     * @param season 季度 (可选, 1-4)
     * @return 包含番剧元数据和进度信息的映射列表
     */
    List<Map<String, Object>> getAnimeListWithProgress(Integer year, Integer season);

    /**
     * 切换特定集数的观看状态 (幂等 Toggle 逻辑)
     *
     * @param animeId      本地番剧 ID
     * @param episodeIndex 集数索引 (通常从 1 开始)
     */
    void toggleEpisode(Long animeId, Integer episodeIndex);

    /**
     * 更新番剧的追番状态 (想看/在看/已完成)
     *
     * @param animeId 本地番剧 ID
     * @param status  目标状态 (0:想看, 1:在看, 2:已完成)
     */
    void updateStatus(Long animeId, Integer status);
}
