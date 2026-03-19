package com.czf.blog.controller;

import com.czf.blog.common.PageData;
import com.czf.blog.common.Result;
import com.czf.blog.dto.NoteDetailVO;
import com.czf.blog.dto.NoteListVO;
import com.czf.blog.dto.NoteSaveDTO;
import com.czf.blog.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 笔记控制器
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * 保存或更新笔记
     */
    @PostMapping("/save")
    public Result<Long> saveNote(@RequestBody NoteSaveDTO dto) {
        return Result.success(noteService.saveOrUpdateNote(dto));
    }

    /**
     * 分页查询笔记列表
     */
    @GetMapping("/list")
    public Result<PageData<NoteListVO>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "moduleType", required = false) Integer moduleType,
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(noteService.getNotePage(pageNum, pageSize, moduleType, status));
    }

    /**
     * 获取笔记详情
     */
    @GetMapping("/{id}")
    public Result<NoteDetailVO> getDetail(@PathVariable("id") Long id) {
        return Result.success(noteService.getNoteDetail(id));
    }
}
