package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 番剧管理控制器
 */
@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
@CrossOrigin
public class AnimeController {

    private final AnimeService animeService;

    /**
     * 搜索 Bangumi 番剧
     */
    @GetMapping("/search")
    public Result<List<BangumiDTOs.SubjectItem>> search(@RequestParam String keyword) {
        return Result.success(animeService.searchBangumi(keyword));
    }

    /**
     * 导入番剧元数据并初始化进度
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
     * 获取本地番剧列表及其进度
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer season) {
        return Result.success(animeService.getAnimeListWithProgress(year, season));
    }

    /**
     * 切换集数观看状态
     */
    @PostMapping("/toggle")
    public Result<Void> toggleEpisode(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer episodeIndex = (Integer) params.get("episodeIndex");
        animeService.toggleEpisode(animeId, episodeIndex);
        return Result.success();
    }

    /**
     * 更新番剧状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer status = (Integer) params.get("status");
        animeService.updateStatus(animeId, status);
        return Result.success();
    }
}
