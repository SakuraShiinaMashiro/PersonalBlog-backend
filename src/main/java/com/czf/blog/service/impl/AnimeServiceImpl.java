package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.entity.AnimeProgress;
import com.czf.blog.entity.AnimeSubject;
import com.czf.blog.mapper.AnimeProgressMapper;
import com.czf.blog.mapper.AnimeSubjectMapper;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author challenge
 */
@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

    private final AnimeSubjectMapper subjectMapper;
    private final AnimeProgressMapper progressMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BGM_API_BASE = "https://api.bgm.tv/v0";
    private static final String USER_AGENT = "SakuraShiinaMashiro/PersonalBlog";

    private <T> T fetchFromBangumi(String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    private <T> T postToBangumi(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        headers.set("Content-Type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    @Override
    public List<BangumiDTOs.SubjectItem> searchBangumi(String keyword) {
        String url = BGM_API_BASE + "/search/subjects";
        Map<String, Object> body = new HashMap<>();
        body.put("keyword", keyword);
        Map<String, Object> filter = new HashMap<>();
        // Type 2 is Anime
        filter.put("type", List.of(2));
        body.put("filter", filter);
        
        BangumiDTOs.SubjectSearchResponse response = postToBangumi(url, body, BangumiDTOs.SubjectSearchResponse.class);
        return response != null ? response.data() : List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importFromBangumi(int bgmId, int airYear, int airSeason) {
        // 1. Fetch details from Bangumi (Strictly follow v0 GET /subjects/{id} specification)
        String url = BGM_API_BASE + "/subjects/" + bgmId;
        BangumiDTOs.SubjectItem item = fetchFromBangumi(url, BangumiDTOs.SubjectItem.class);

        // 2. Save Subject
        AnimeSubject subject = new AnimeSubject();
        subject.setBgmId(bgmId);
        if (item == null) {
            throw new RuntimeException("未能获取到番剧详情，bgmId: " + bgmId);
        }
        subject.setTitle(item.getDisplayName());
        subject.setImageUrl(item.images() != null ? item.images().large() : "");
        subject.setEps(item.eps());
        subject.setAirYear(airYear);
        subject.setAirSeason(airSeason);
        // Default to 'Watching'
        subject.setStatus(1);
        subjectMapper.insert(subject);

        // 3. Initialize Progress
        AnimeProgress progress = new AnimeProgress();
        progress.setAnimeId(subject.getId());
        progress.setWatchedEps(new ArrayList<>());
        progressMapper.insert(progress);
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
    @Transactional(rollbackFor = Exception.class)
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
