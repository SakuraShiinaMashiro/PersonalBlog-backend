package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.czf.blog.common.PageData;
import com.czf.blog.dto.ContentDetailVO;
import com.czf.blog.dto.ContentListVO;
import com.czf.blog.dto.ContentSaveDTO;
import com.czf.blog.entity.BlogContent;
import com.czf.blog.entity.BlogContentTag;
import com.czf.blog.entity.BlogTag;
import com.czf.blog.mapper.BlogContentMapper;
import com.czf.blog.mapper.BlogContentTagMapper;
import com.czf.blog.mapper.BlogTagMapper;
import com.czf.blog.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 内容服务实现类
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final BlogContentMapper contentMapper;
    private final BlogTagMapper tagMapper;
    private final BlogContentTagMapper contentTagMapper;

    private static final Pattern MARKDOWN_PATTERN = Pattern.compile("<[^>]+>|#|\\*|`|>|-|\\[|\\]|\\(|\\)|!|\\n");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateContent(ContentSaveDTO dto) {
        BlogContent content = new BlogContent();
        content.setId(dto.id());
        content.setTitle(dto.title());
        content.setContent(dto.content());
        content.setModuleType(dto.moduleType());
        content.setStatus(dto.status());
        content.setCoverUrl(dto.coverUrl());

        // 处理摘要
        if (StringUtils.hasText(dto.summary())) {
            content.setSummary(dto.summary());
        } else {
            // 自动提取摘要, 剥离Markdown符号, 取前150字
            String plainText = MARKDOWN_PATTERN.matcher(dto.content()).replaceAll(" ");
            plainText = plainText.replaceAll("\\s+", " ").trim();
            content.setSummary(plainText.length() > 150 ? plainText.substring(0, 150) + "..." : plainText);
        }

        if (content.getId() == null) {
            content.setViews(0);
            contentMapper.insert(content);
        } else {
            contentMapper.updateById(content);
            // 先清理旧标签关联
            contentTagMapper.delete(new LambdaQueryWrapper<BlogContentTag>()
                    .eq(BlogContentTag::getContentId, content.getId()));
        }

        // 处理标签
        if (dto.tags() != null && !dto.tags().isEmpty()) {
            for (String tagName : dto.tags()) {
                String cleanName = tagName.trim();
                // 查找标签是否存在
                BlogTag tag = tagMapper.selectOne(new LambdaQueryWrapper<BlogTag>()
                        .eq(BlogTag::getName, cleanName));
                if (tag == null) {
                    tag = new BlogTag();
                    tag.setName(cleanName);
                    tagMapper.insert(tag);
                }
                // 插入关联表
                BlogContentTag contentTag = new BlogContentTag();
                contentTag.setContentId(content.getId());
                contentTag.setTagId(tag.getId());
                contentTagMapper.insert(contentTag);
            }
        }

        return content.getId();
    }

    @Override
    public PageData<ContentListVO> getContentPage(int pageNum, int pageSize, Integer moduleType, Integer status) {
        Page<BlogContent> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BlogContent> wrapper = new LambdaQueryWrapper<>();
        if (moduleType != null) {
            wrapper.eq(BlogContent::getModuleType, moduleType);
        }
        if (status != null) {
            wrapper.eq(BlogContent::getStatus, status);
        }
        wrapper.orderByDesc(BlogContent::getCreateTime);

        contentMapper.selectPage(page, wrapper);

        List<ContentListVO> list = new ArrayList<>();
        for (BlogContent c : page.getRecords()) {
            List<String> tags = getTagsByContentId(c.getId());
            list.add(new ContentListVO(
                    c.getId(), c.getTitle(), c.getSummary(), c.getModuleType(), c.getStatus(),
                    c.getCoverUrl(), c.getViews(), c.getCreateTime(), c.getUpdateTime(), tags
            ));
        }

        return new PageData<>(list, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public ContentDetailVO getContentDetail(Long id) {
        BlogContent content = contentMapper.selectById(id);
        if (content == null) {
            throw new RuntimeException("文章不存在"); // 根据规范，应跑出自定业务异常，这里用RuntimeException简化
        }

        // 异步或直接增加阅读量
        BlogContent updateViews = new BlogContent();
        updateViews.setId(id);
        updateViews.setViews(content.getViews() + 1);
        contentMapper.updateById(updateViews);

        List<String> tags = getTagsByContentId(id);

        return new ContentDetailVO(
                content.getId(), content.getTitle(), content.getContent(), content.getModuleType(),
                content.getStatus(), content.getCoverUrl(), content.getViews() + 1,
                content.getCreateTime(), content.getUpdateTime(), tags
        );
    }

    private List<String> getTagsByContentId(Long contentId) {
        List<BlogContentTag> contentTags = contentTagMapper.selectList(new LambdaQueryWrapper<BlogContentTag>()
                .eq(BlogContentTag::getContentId, contentId));
        if (contentTags.isEmpty()) return new ArrayList<>();

        List<Long> tagIds = contentTags.stream().map(BlogContentTag::getTagId).collect(Collectors.toList());
        if (tagIds.isEmpty()) return new ArrayList<>();
        List<BlogTag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream().map(BlogTag::getName).collect(Collectors.toList());
    }
}
