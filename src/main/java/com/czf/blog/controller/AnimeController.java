package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 追番模块控制器
 * @author Gemini
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
@CrossOrigin
public class AnimeController {

    private final AnimeService animeService;

    /**
     * 搜索 Bangumi 番剧
     * @param keyword 搜索关键词
     * @return 匹配的番剧列表
     */
    @GetMapping("/search")
    public Result<List<BangumiDTOs.SubjectItem>> search(@RequestParam(value = "keyword") String keyword) {
        return Result.success(animeService.searchBangumi(keyword));
    }

    /**
     * 导入番剧元数据并初始化本地追番进度
     * @param params 包含 bgmId, airYear, airSeason 的映射
     * @return 统一返回结果
     */
    @PostMapping("/import")
    public Result<Void> importAnime(@RequestBody Map<String, Object> params) {
        int bgmId = (int) params.get("bgmId");
        int airYear = (int) params.get("airYear");
        int airSeason = (int) params.get("airSeason");
        animeService.importFromBangumi(bgmId, airYear, airSeason);
        return Result.success();
    }

    /**
     * 获取指定季度下的本地番剧列表及其追番进度
     * @param year 年份
     * @param season 季度 (1-4)
     * @return 包含番剧元数据和进度信息的列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "season", required = false) Integer season) {
        return Result.success(animeService.getAnimeListWithProgress(year, season));
    }

    /**
     * 切换特定集数的观看状态 (幂等 Toggle 逻辑)
     * @param params 包含 animeId, episodeIndex 的映射
     * @return 统一返回结果
     */
    @PostMapping("/toggle")
    public Result<Void> toggleEpisode(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer episodeIndex = (Integer) params.get("episodeIndex");
        animeService.toggleEpisode(animeId, episodeIndex);
        return Result.success();
    }

    /**
     * 更新番剧的追番状态
     * @param params 包含 animeId, status 的映射
     * @return 统一返回结果
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer status = (Integer) params.get("status");
        animeService.updateStatus(animeId, status);
        return Result.success();
    }
}
