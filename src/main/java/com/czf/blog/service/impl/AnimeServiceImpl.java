package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.entity.AnimeProgress;
import com.czf.blog.entity.AnimeSubject;
import com.czf.blog.mapper.AnimeProgressMapper;
import com.czf.blog.mapper.AnimeSubjectMapper;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 追番模块业务实现类
 * @author Gemini
 * @date 2026-03-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

    private final AnimeSubjectMapper subjectMapper;
    private final AnimeProgressMapper progressMapper;
    private final RestClient bangumiRestClient;

    @Override
    public List<BangumiDTOs.SubjectItem> searchBangumi(String keyword) {
        log.info("Searching anime on Bangumi with keyword: {}", keyword);
        try {
            // 使用 GET /search/subjects 接口
            // 注意：Bangumi v0 的搜索接口建议使用 POST 以支持更复杂的过滤，
            // 但为了简化并符合 spec 中的 GET 设计，我们也可以使用查询参数。
            // 实际上 Bangumi v0 搜索建议用 POST，这里我们根据实际情况调整。
            
            BangumiDTOs.SubjectSearchResponse response = bangumiRestClient.post()
                    .uri("/search/subjects")
                    .body(Map.of(
                            "keyword", keyword,
                            // 2 为动画
                            "filter", Map.of("type", List.of(2))
                    ))
                    .retrieve()
                    .body(BangumiDTOs.SubjectSearchResponse.class);

            return response != null ? response.data() : List.of();
        } catch (Exception e) {
            log.error("Failed to search Bangumi with keyword: {}", keyword, e);
            // 按照设计决策，外部服务失败时返回空列表或友好错误
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importFromBangumi(int bgmId, int airYear, int airSeason) {
        log.info("Importing anime from Bangumi, bgmId: {}", bgmId);
        try {
            BangumiDTOs.SubjectItem item = bangumiRestClient.get()
                    .uri("/subjects/{id}", bgmId)
                    .retrieve()
                    .body(BangumiDTOs.SubjectItem.class);

            if (item == null) {
                throw new RuntimeException("未能获取到番剧详情，bgmId: " + bgmId);
            }

            // 保存番剧元数据
            AnimeSubject subject = new AnimeSubject();
            subject.setBgmId(bgmId);
            subject.setTitle(item.getDisplayName());
            subject.setImageUrl(item.images() != null ? item.images().large() : "");
            subject.setEps(item.eps());
            subject.setAirYear(airYear);
            subject.setAirSeason(airSeason);
            subject.setStatus(1); // 默认为“在看”
            subjectMapper.insert(subject);

            // 初始化进度
            AnimeProgress progress = new AnimeProgress();
            progress.setAnimeId(subject.getId());
            progress.setWatchedEps(new ArrayList<>());
            progressMapper.insert(progress);

        } catch (Exception e) {
            log.error("Failed to import anime from Bangumi, bgmId: {}", bgmId, e);
            throw new RuntimeException("导入番剧失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAnimeListWithProgress(Integer year, Integer season) {
        LambdaQueryWrapper<AnimeSubject> queryWrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            queryWrapper.eq(AnimeSubject::getAirYear, year);
        }
        if (season != null) {
            queryWrapper.eq(AnimeSubject::getAirSeason, season);
        }
        queryWrapper.orderByDesc(AnimeSubject::getCreateTime);

        List<AnimeSubject> subjects = subjectMapper.selectList(queryWrapper);
        return subjects.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("subject", s);
            map.put("progress", progressMapper.selectById(s.getId()));
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void toggleEpisode(Long animeId, Integer episodeIndex) {
        AnimeProgress progress = progressMapper.selectById(animeId);
        if (progress == null) {
            return;
        }

        List<Integer> watched = progress.getWatchedEps();
        if (watched == null) {
            watched = new ArrayList<>();
        }

        if (watched.contains(episodeIndex)) {
            watched.remove(episodeIndex);
        } else {
            watched.add(episodeIndex);
        }

        progress.setWatchedEps(watched);
        progressMapper.updateById(progress);
    }

    @Override
    public void updateStatus(Long animeId, Integer status) {
        AnimeSubject subject = new AnimeSubject();
        subject.setId(animeId);
        subject.setStatus(status);
        subjectMapper.updateById(subject);
    }
}
