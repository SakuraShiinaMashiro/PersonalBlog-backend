package com.czf.blog.controller;

import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anime")
@RequiredArgsConstructor
@CrossOrigin // Simple cross-origin support
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping("/search")
    public List<BangumiDTOs.SubjectItem> search(@RequestParam String keyword) {
        return animeService.searchBangumi(keyword);
    }

    @PostMapping("/import")
    public void importAnime(@RequestBody Map<String, Object> params) {
        int bgmId = (int) params.get("bgmId");
        int airYear = (int) params.get("airYear");
        int airSeason = (int) params.get("airSeason");
        animeService.importFromBangumi(bgmId, airYear, airSeason);
    }

    @GetMapping("/list")
    public List<Map<String, Object>> getList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer season) {
        return animeService.getAnimeListWithProgress(year, season);
    }

    @PostMapping("/toggle")
    public void toggleEpisode(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer episodeIndex = (Integer) params.get("episodeIndex");
        animeService.toggleEpisode(animeId, episodeIndex);
    }

    @PutMapping("/status")
    public void updateStatus(@RequestBody Map<String, Object> params) {
        Long animeId = Long.valueOf(params.get("animeId").toString());
        Integer status = (Integer) params.get("status");
        animeService.updateStatus(animeId, status);
    }
}
