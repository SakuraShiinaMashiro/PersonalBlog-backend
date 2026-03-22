package com.czf.blog.controller;

import com.czf.blog.common.Result;
import com.czf.blog.dto.AnimeImportDTO;
import com.czf.blog.dto.AnimeImportResult;
import com.czf.blog.dto.AnimeProgressActionDTO;
import com.czf.blog.dto.AnimeSeenToEpisodeDTO;
import com.czf.blog.dto.AnimeToggleEpisodeDTO;
import com.czf.blog.dto.AnimeTrackDateDTO;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public Result<AnimeImportResult> importAnime(@RequestBody AnimeImportDTO dto) {
        return Result.success(animeService.importFromBangumi(dto.bgmId(), dto.trackDate()));
    }

    @Operation(summary = "获取追番列表", description = "获取已导入的番剧列表，支持按年份和季度筛选，可选参数不传则返回全部")
    @Parameters({
            @Parameter(name = "year", description = "播出年份筛选", in = ParameterIn.QUERY),
            @Parameter(name = "season", description = "播出季度筛选（1-4）", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "追番状态筛选（0-想看，1-在看，2-已看完）", in = ParameterIn.QUERY),
            @Parameter(name = "trackDateStart", description = "开始追番起始日期（yyyy-MM-dd）", in = ParameterIn.QUERY),
            @Parameter(name = "trackDateEnd", description = "开始追番结束日期（yyyy-MM-dd）", in = ParameterIn.QUERY)
    })
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "season", required = false) Integer season,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "trackDateStart", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate trackDateStart,
            @RequestParam(value = "trackDateEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate trackDateEnd) {
        return Result.success(animeService.getAnimeListWithProgress(year, season, status, trackDateStart, trackDateEnd));
    }

    @Operation(summary = "切换单集观看状态", description = "切换指定集数的已看/未看状态（幂等操作：已看则移除，未看则添加）")
    @PostMapping("/toggle")
    public Result<Void> toggleEpisode(@RequestBody AnimeToggleEpisodeDTO dto) {
        animeService.toggleEpisode(dto.animeId(), dto.episodeIndex());
        return Result.success();
    }

    @Operation(summary = "快捷设置看到第N集", description = "将追番进度覆盖为 1..N，并自动更新状态")
    @PutMapping("/progress/seen-to")
    public Result<Void> seenToEpisode(@RequestBody AnimeSeenToEpisodeDTO dto) {
        animeService.seenToEpisode(dto.animeId(), dto.episode());
        return Result.success();
    }

    @Operation(summary = "快捷一键看完", description = "将追番进度覆盖为 1..总集数，并自动更新状态")
    @PutMapping("/progress/complete")
    public Result<Void> completeAnime(@RequestBody AnimeProgressActionDTO dto) {
        animeService.completeAnime(dto.animeId());
        return Result.success();
    }

    @Operation(summary = "快捷重置进度", description = "将追番进度清空，并自动更新状态")
    @PutMapping("/progress/reset")
    public Result<Void> resetProgress(@RequestBody AnimeProgressActionDTO dto) {
        animeService.resetProgress(dto.animeId());
        return Result.success();
    }

    @Operation(summary = "修改开始追番时间", description = "更新指定番剧的开始追番时间，不影响进度与状态")
    @PutMapping("/track-date")
    public Result<Void> updateTrackDate(@RequestBody AnimeTrackDateDTO dto) {
        animeService.updateTrackDate(dto.animeId(), dto.trackDate());
        return Result.success();
    }

    @Operation(summary = "删除追番记录", description = "按本地 animeId 删除追番记录，关联进度将级联删除")
    @DeleteMapping("/{animeId}")
    public Result<Void> deleteAnime(@PathVariable("animeId") Long animeId) {
        animeService.deleteAnime(animeId);
        return Result.success();
    }
}
