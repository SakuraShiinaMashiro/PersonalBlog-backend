package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.AnimeImportResult;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 追番模块控制器
 *
 * @author Gemini
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "追番管理", description = "番剧搜索、导入、列表查询与进度管理")
public class AnimeController {

    private final AnimeService animeService;

    @Operation(summary = "搜索 Bangumi 番剧", description = "根据关键词搜索 Bangumi 番剧，返回匹配结果列表")
    @Parameters({
            @Parameter(name = "keyword", description = "搜索关键词", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/search")
    public Result<List<BangumiDTOs.SubjectItem>> search(@RequestParam(value = "keyword") String keyword) {
        return Result.success(animeService.searchBangumi(keyword));
    }

    @Operation(summary = "导入番剧", description = "从 Bangumi 导入番剧元数据到本地库并初始化追番进度")
    @PostMapping("/import")
    public Result<AnimeImportResult> importAnime(@RequestBody Map<String, Object> params) {
        int bgmId = Integer.parseInt(params.get("bgmId").toString());
        return Result.success(animeService.importFromBangumi(bgmId));
    }

    @Operation(summary = "获取追番列表", description = "获取已导入的番剧列表，支持按年份和季度筛选，可选参数不传则返回全部")
    @Parameters({
            @Parameter(name = "year", description = "播出年份筛选", in = ParameterIn.QUERY),
            @Parameter(name = "season", description = "播出季度筛选（1-4）", in = ParameterIn.QUERY)
    })
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "season", required = false) Integer season) {
        return Result.success(animeService.getAnimeListWithProgress(year, season));
    }

    @Operation(summary = "切换单集观看状态", description = "切换指定集数的已看/未看状态（幂等操作：已看则移除，未看则添加）")
    @PostMapping("/toggle")
    public Result<Void> toggleEpisode(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer episodeIndex = Integer.parseInt(params.get("episodeIndex").toString());
        animeService.toggleEpisode(animeId, episodeIndex);
        return Result.success();
    }

    @Operation(summary = "快捷设置看到第N集", description = "将追番进度覆盖为 1..N，并自动更新状态")
    @PutMapping("/progress/seen-to")
    public Result<Void> seenToEpisode(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer episode = Integer.parseInt(params.get("episode").toString());
        animeService.seenToEpisode(animeId, episode);
        return Result.success();
    }

    @Operation(summary = "快捷一键看完", description = "将追番进度覆盖为 1..总集数，并自动更新状态")
    @PutMapping("/progress/complete")
    public Result<Void> completeAnime(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        animeService.completeAnime(animeId);
        return Result.success();
    }

    @Operation(summary = "快捷重置进度", description = "将追番进度清空，并自动更新状态")
    @PutMapping("/progress/reset")
    public Result<Void> resetProgress(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        animeService.resetProgress(animeId);
        return Result.success();
    }
}
