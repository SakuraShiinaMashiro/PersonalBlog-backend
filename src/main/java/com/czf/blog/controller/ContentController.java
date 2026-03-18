package com.czf.blog.controller;

import com.czf.blog.common.PageData;
import com.czf.blog.common.Result;
import com.czf.blog.dto.ContentDetailVO;
import com.czf.blog.dto.ContentListVO;
import com.czf.blog.dto.ContentSaveDTO;
import com.czf.blog.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 内容控制器
 * 
 * @author Gemini
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    /**
     * 保存或更新内容
     */
    @PostMapping("/save")
    public Result<Long> saveContent(@RequestBody ContentSaveDTO dto) {
        return Result.success(contentService.saveOrUpdateContent(dto));
    }

    /**
     * 分页查询内容列表
     */
    @GetMapping("/list")
    public Result<PageData<ContentListVO>> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer moduleType,
            @RequestParam(required = false) Integer status) {
        return Result.success(contentService.getContentPage(pageNum, pageSize, moduleType, status));
    }

    /**
     * 获取内容详情
     */
    @GetMapping("/{id}")
    public Result<ContentDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(contentService.getContentDetail(id));
    }
}
