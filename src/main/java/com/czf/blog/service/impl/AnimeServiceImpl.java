package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.dto.AnimeImportResult;
import com.czf.blog.dto.BangumiDTOs;
import com.czf.blog.entity.AnimeProgress;
import com.czf.blog.entity.AnimeSubject;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.mapper.AnimeProgressMapper;
import com.czf.blog.mapper.AnimeSubjectMapper;
import com.czf.blog.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.net.SocketTimeoutException;
import java.util.stream.IntStream;
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
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            throw new BizException(BizErrorCode.KEYWORD_EMPTY);
        }

        log.info("Searching anime on Bangumi with keyword: {}", normalizedKeyword);
        try {
            BangumiDTOs.SubjectSearchResponse response = bangumiRestClient.post()
                    .uri("/search/subjects")
                    .body(Map.of(
                            "keyword", normalizedKeyword,
                            "filter", Map.of("type", List.of(2))
                    ))
                    .retrieve()
                    .body(BangumiDTOs.SubjectSearchResponse.class);

            return response != null ? response.data() : List.of();
        } catch (ResourceAccessException e) {
            log.error("Bangumi search timeout or network issue, keyword: {}", normalizedKeyword, e);
            if (isTimeoutException(e)) {
                throw new BizException(BizErrorCode.BANGUMI_SEARCH_TIMEOUT);
            }
            throw new BizException(BizErrorCode.BANGUMI_SEARCH_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("Bangumi search request failed, keyword: {}", normalizedKeyword, e);
            throw new BizException(BizErrorCode.BANGUMI_SEARCH_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Unexpected Bangumi search error, keyword: {}", normalizedKeyword, e);
            throw new BizException(BizErrorCode.BANGUMI_SEARCH_UNAVAILABLE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimeImportResult importFromBangumi(int bgmId, LocalDate trackDate) {
        log.info("Importing anime from Bangumi, bgmId: {}", bgmId);
        try {
            BangumiDTOs.SubjectItem item = bangumiRestClient.get()
                    .uri("/subjects/{id}", bgmId)
                    .retrieve()
                    .body(BangumiDTOs.SubjectItem.class);

            if (item == null) {
                throw new BizException("未能获取到番剧详情，bgmId: " + bgmId);
            }

            LocalDate airDate = parseAirDate(item.date());
            AnimeSubject existingSubject = subjectMapper.selectOne(new LambdaQueryWrapper<AnimeSubject>()
                    .eq(AnimeSubject::getBgmId, bgmId)
                    .last("LIMIT 1"));

            if (existingSubject == null) {
                AnimeSubject subject = new AnimeSubject();
                subject.setBgmId(bgmId);
                subject.setTitle(item.getDisplayName());
                subject.setImageUrl(item.images() != null ? item.images().large() : "");
                subject.setEps(item.eps());
                subject.setAirDate(airDate);
                subject.setAirYear(airDate != null ? airDate.getYear() : null);
                subject.setAirSeason(airDate != null ? resolveSeason(airDate.getMonthValue()) : null);
                subjectMapper.insert(subject);

                AnimeProgress progress = new AnimeProgress();
                progress.setAnimeId(subject.getId());
                progress.setStatus(0);
                progress.setWatchedEps(new ArrayList<>());
                progress.setTrackDate(trackDate != null ? trackDate : LocalDate.now());
                progressMapper.insert(progress);
                return AnimeImportResult.created();
            }

            existingSubject.setTitle(item.getDisplayName());
            existingSubject.setImageUrl(item.images() != null ? item.images().large() : "");
            existingSubject.setEps(item.eps());
            existingSubject.setAirDate(airDate);
            existingSubject.setAirYear(airDate != null ? airDate.getYear() : null);
            existingSubject.setAirSeason(airDate != null ? resolveSeason(airDate.getMonthValue()) : null);
            subjectMapper.updateById(existingSubject);
            return AnimeImportResult.updated();

        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to import anime from Bangumi, bgmId: {}", bgmId, e);
            throw new BizException("导入番剧失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAnimeListWithProgress(Integer year, Integer season, Integer status,
                                                              LocalDate trackDateStart, LocalDate trackDateEnd) {
        validateListQueryParams(season, status, trackDateStart, trackDateEnd);

        LambdaQueryWrapper<AnimeSubject> queryWrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            queryWrapper.eq(AnimeSubject::getAirYear, year);
        }
        if (season != null) {
            queryWrapper.eq(AnimeSubject::getAirSeason, season);
        }

        List<AnimeSubject> subjects = subjectMapper.selectList(queryWrapper);

        return subjects.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("subject", s);
            map.put("progress", progressMapper.selectById(s.getId()));
            return map;
        }).filter(item -> matchStatus(item, status))
                .filter(item -> matchTrackDateRange(item, trackDateStart, trackDateEnd))
                .sorted(Comparator
                        .comparing(
                                (Map<String, Object> item) -> {
                                    AnimeProgress progress = (AnimeProgress) item.get("progress");
                                    return progress == null ? null : progress.getLastWatchAt();
                                },
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                item -> {
                                    AnimeProgress progress = (AnimeProgress) item.get("progress");
                                    return progress == null ? null : progress.getTrackDate();
                                },
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                item -> {
                                    AnimeSubject subject = (AnimeSubject) item.get("subject");
                                    return subject == null ? null : subject.getId();
                                },
                                Comparator.nullsLast(Comparator.reverseOrder())
                        ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleEpisode(Long animeId, Integer episodeIndex) {
        AnimeSubject subject = subjectMapper.selectById(animeId);
        if (subject == null || episodeIndex == null) {
            return;
        }

        Integer totalEpisodes = subject.getEps();
        if (totalEpisodes == null || totalEpisodes <= 0 || episodeIndex < 1 || episodeIndex > totalEpisodes) {
            return;
        }

        AnimeProgress progress = progressMapper.selectById(animeId);
        boolean isNewProgress = progress == null;
        if (isNewProgress) {
            progress = new AnimeProgress();
            progress.setAnimeId(animeId);
            progress.setTrackDate(LocalDate.now());
            progress.setWatchedEps(new ArrayList<>());
        }

        Set<Integer> watchedSet = progress.getWatchedEps() == null
                ? new HashSet<>()
                : progress.getWatchedEps().stream()
                .filter(Objects::nonNull)
                .filter(ep -> ep >= 1 && ep <= totalEpisodes)
                .collect(Collectors.toSet());

        if (watchedSet.contains(episodeIndex)) {
            watchedSet.remove(episodeIndex);
        } else {
            watchedSet.add(episodeIndex);
        }

        List<Integer> normalizedWatched = watchedSet.stream().sorted().collect(Collectors.toList());
        int watchedCount = normalizedWatched.size();

        progress.setWatchedEps(normalizedWatched);
        progress.setStatus(calculateStatus(watchedCount, totalEpisodes));
        progress.setLastWatchAt(LocalDateTime.now());

        if (isNewProgress) {
            progressMapper.insert(progress);
        } else {
            progressMapper.updateById(progress);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void seenToEpisode(Long animeId, Integer episode) {
        AnimeSubject subject = requireSubject(animeId);
        Integer totalEpisodes = subject.getEps();
        if (totalEpisodes == null || totalEpisodes <= 0) {
            throw new BizException("总集数无效，无法快捷更新到指定集");
        }
        if (episode == null || episode < 1) {
            throw new BizException("目标集数必须大于等于1");
        }
        int finalEpisode = Math.min(episode, totalEpisodes);
        List<Integer> watchedEpisodes = IntStream.rangeClosed(1, finalEpisode).boxed().collect(Collectors.toList());
        applyProgress(subject, watchedEpisodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAnime(Long animeId) {
        AnimeSubject subject = requireSubject(animeId);
        Integer totalEpisodes = subject.getEps();
        if (totalEpisodes == null || totalEpisodes <= 0) {
            throw new BizException("总集数无效，无法执行一键看完");
        }
        List<Integer> watchedEpisodes = IntStream.rangeClosed(1, totalEpisodes).boxed().collect(Collectors.toList());
        applyProgress(subject, watchedEpisodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetProgress(Long animeId) {
        AnimeSubject subject = requireSubject(animeId);
        applyProgress(subject, List.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTrackDate(Long animeId, LocalDate trackDate) {
        if (trackDate == null) {
            throw new BizException("追番时间不能为空");
        }
        if (trackDate.isAfter(LocalDate.now())) {
            throw new BizException("追番时间不能晚于今天");
        }

        AnimeSubject subject = requireSubject(animeId);
        AnimeProgress progress = progressMapper.selectById(subject.getId());

        if (progress == null) {
            progress = new AnimeProgress();
            progress.setAnimeId(subject.getId());
            progress.setStatus(0);
            progress.setWatchedEps(new ArrayList<>());
            progress.setTrackDate(trackDate);
            progressMapper.insert(progress);
            return;
        }

        progress.setTrackDate(trackDate);
        progressMapper.updateById(progress);
    }

    private void validateListQueryParams(Integer season, Integer status, LocalDate trackDateStart, LocalDate trackDateEnd) {
        if (season != null && (season < 1 || season > 4)) {
            throw new BizException("参数错误：season 必须在 1-4 之间");
        }
        if (status != null && (status < 0 || status > 2)) {
            throw new BizException("参数错误：status 必须在 0-2 之间");
        }
        if (trackDateStart != null && trackDateEnd != null && trackDateStart.isAfter(trackDateEnd)) {
            throw new BizException("参数错误：开始日期不能晚于结束日期");
        }
    }

    private boolean matchStatus(Map<String, Object> item, Integer status) {
        if (status == null) {
            return true;
        }
        AnimeProgress progress = (AnimeProgress) item.get("progress");
        return progress != null && Objects.equals(progress.getStatus(), status);
    }

    private boolean matchTrackDateRange(Map<String, Object> item, LocalDate trackDateStart, LocalDate trackDateEnd) {
        if (trackDateStart == null && trackDateEnd == null) {
            return true;
        }
        AnimeProgress progress = (AnimeProgress) item.get("progress");
        if (progress == null || progress.getTrackDate() == null) {
            return false;
        }
        LocalDate trackDate = progress.getTrackDate();
        if (trackDateStart != null && trackDate.isBefore(trackDateStart)) {
            return false;
        }
        if (trackDateEnd != null && trackDate.isAfter(trackDateEnd)) {
            return false;
        }
        return true;
    }

    private AnimeSubject requireSubject(Long animeId) {
        AnimeSubject subject = subjectMapper.selectById(animeId);
        if (subject == null) {
            throw new BizException("番剧不存在");
        }
        return subject;
    }

    private void applyProgress(AnimeSubject subject, List<Integer> watchedEpisodes) {
        Integer totalEpisodes = subject.getEps();
        int maxEpisode = (totalEpisodes != null && totalEpisodes > 0) ? totalEpisodes : Integer.MAX_VALUE;

        Long animeId = subject.getId();
        AnimeProgress progress = progressMapper.selectById(animeId);
        boolean isNewProgress = progress == null;
        if (isNewProgress) {
            progress = new AnimeProgress();
            progress.setAnimeId(animeId);
            progress.setTrackDate(LocalDate.now());
        }

        List<Integer> normalizedWatched = watchedEpisodes.stream()
                .filter(Objects::nonNull)
                .filter(ep -> ep >= 1 && ep <= maxEpisode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        progress.setWatchedEps(normalizedWatched);
        progress.setStatus(calculateStatus(normalizedWatched.size(), totalEpisodes));
        progress.setLastWatchAt(LocalDateTime.now());

        if (isNewProgress) {
            progressMapper.insert(progress);
        } else {
            progressMapper.updateById(progress);
        }
    }

    private Integer calculateStatus(int watchedCount, Integer totalEpisodes) {
        if (watchedCount <= 0) {
            return 0;
        }
        if (totalEpisodes != null && totalEpisodes > 0 && watchedCount >= totalEpisodes) {
            return 2;
        }
        return 1;
    }

    private LocalDate parseAirDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        String normalized = rawDate.trim();
        if (normalized.length() >= 10) {
            normalized = normalized.substring(0, 10);
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse air date from Bangumi: {}", rawDate);
            return null;
        }
    }

    private Integer resolveSeason(int month) {
        if (month <= 3) {
            return 1;
        }
        if (month <= 6) {
            return 2;
        }
        if (month <= 9) {
            return 3;
        }
        return 4;
    }

    private boolean isTimeoutException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
