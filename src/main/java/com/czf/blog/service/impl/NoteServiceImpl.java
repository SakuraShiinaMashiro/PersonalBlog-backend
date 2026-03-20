package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.czf.blog.common.PageData;
import com.czf.blog.dto.NoteDetailVO;
import com.czf.blog.dto.NoteListVO;
import com.czf.blog.dto.NoteSaveDTO;
import com.czf.blog.entity.BlogNote;
import com.czf.blog.entity.BlogNoteTag;
import com.czf.blog.entity.BlogTag;
import com.czf.blog.mapper.BlogNoteMapper;
import com.czf.blog.mapper.BlogNoteTagMapper;
import com.czf.blog.mapper.BlogTagMapper;
import com.czf.blog.service.NoteService;
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
 * 笔记服务实现类
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final BlogNoteMapper noteMapper;
    private final BlogTagMapper tagMapper;
    private final BlogNoteTagMapper noteTagMapper;

    private static final Pattern MARKDOWN_PATTERN = Pattern.compile("<[^>]+>|#|\\*|`|>|-|\\[|\\]|\\(|\\)|!|\\n");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateNote(NoteSaveDTO dto) {
        BlogNote note = new BlogNote();
        note.setId(dto.id());
        note.setTitle(dto.title());
        note.setContent(dto.content());
        note.setModuleType(dto.moduleType());
        note.setStatus(dto.status());
        note.setCoverUrl(dto.coverUrl());

        // 处理摘要
        if (StringUtils.hasText(dto.summary())) {
            note.setSummary(dto.summary());
        } else {
            // 自动提取摘要, 剥离Markdown符号, 取前150字
            String plainText = MARKDOWN_PATTERN.matcher(dto.content()).replaceAll(" ");
            plainText = plainText.replaceAll("\\s+", " ").trim();
            note.setSummary(plainText.length() > 150 ? plainText.substring(0, 150) + "..." : plainText);
        }

        if (note.getId() == null) {
            note.setViews(0);
            noteMapper.insert(note);
        } else {
            noteMapper.updateById(note);
            // 先清理旧标签关联
            noteTagMapper.delete(new LambdaQueryWrapper<BlogNoteTag>()
                    .eq(BlogNoteTag::getNoteId, note.getId()));
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
                BlogNoteTag noteTag = new BlogNoteTag();
                noteTag.setNoteId(note.getId());
                noteTag.setTagId(tag.getId());
                noteTagMapper.insert(noteTag);
            }
        }

        return note.getId();
    }

    @Override
    public PageData<NoteListVO> getNotePage(int pageNum, int pageSize, Integer moduleType, Integer status) {
        Page<BlogNote> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BlogNote> wrapper = new LambdaQueryWrapper<>();
        if (moduleType != null) {
            wrapper.eq(BlogNote::getModuleType, moduleType);
        }
        if (status != null) {
            wrapper.eq(BlogNote::getStatus, status);
        }
        wrapper.orderByDesc(BlogNote::getCreateTime);

        noteMapper.selectPage(page, wrapper);

        List<NoteListVO> list = new ArrayList<>();
        for (BlogNote n : page.getRecords()) {
            List<String> tags = getTagsByNoteId(n.getId());
            list.add(new NoteListVO(
                    n.getId(), n.getTitle(), n.getSummary(), n.getModuleType(), n.getStatus(),
                    n.getCoverUrl(), n.getViews(), n.getCreateTime(), n.getUpdateTime(), tags
            ));
        }

        return new PageData<>(list, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public NoteDetailVO getNoteDetail(Long id) {
        BlogNote note = noteMapper.selectById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        // 异步或直接增加阅读量
        BlogNote updateViews = new BlogNote();
        updateViews.setId(id);
        updateViews.setViews(note.getViews() + 1);
        noteMapper.updateById(updateViews);

        List<String> tags = getTagsByNoteId(id);

        return new NoteDetailVO(
                note.getId(), note.getTitle(), note.getContent(), note.getModuleType(),
                note.getStatus(), note.getCoverUrl(), note.getViews() + 1,
                note.getCreateTime(), note.getUpdateTime(), tags
        );
    }

    private List<String> getTagsByNoteId(Long noteId) {
        List<BlogNoteTag> noteTags = noteTagMapper.selectList(new LambdaQueryWrapper<BlogNoteTag>()
                .eq(BlogNoteTag::getNoteId, noteId));
        if (noteTags.isEmpty()) return new ArrayList<>();

        List<Long> tagIds = noteTags.stream().map(BlogNoteTag::getTagId).collect(Collectors.toList());
        if (tagIds.isEmpty()) return new ArrayList<>();
        List<BlogTag> tags = tagMapper.selectByIds(tagIds);
        return tags.stream().map(BlogTag::getName).collect(Collectors.toList());
    }
}
