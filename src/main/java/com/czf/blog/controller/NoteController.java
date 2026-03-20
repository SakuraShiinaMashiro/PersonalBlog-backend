package com.czf.blog.controller;

import com.czf.blog.common.PageData;
import com.czf.blog.common.Result;
import com.czf.blog.dto.NoteDetailVO;
import com.czf.blog.dto.NoteListVO;
import com.czf.blog.dto.NoteSaveDTO;
import com.czf.blog.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "笔记管理", description = "笔记的增删改查与分页查询")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "保存或更新笔记", description = "新建或更新笔记，id 为空时为新增，id 存在时为更新")
    @PostMapping("/save")
    public Result<Long> saveNote(@RequestBody NoteSaveDTO dto) {
        return Result.success(noteService.saveOrUpdateNote(dto));
    }

    @Operation(summary = "分页查询笔记列表", description = "按模块类型和状态筛选，支持分页")
    @Parameters({
            @Parameter(name = "pageNum", description = "页码（默认 1）", in = ParameterIn.QUERY),
            @Parameter(name = "pageSize", description = "每页条数（默认 10）", in = ParameterIn.QUERY),
            @Parameter(name = "moduleType", description = "模块类型筛选", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "状态筛选（0-草稿，1-发布）", in = ParameterIn.QUERY)
    })
    @GetMapping("/list")
    public Result<PageData<NoteListVO>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "moduleType", required = false) Integer moduleType,
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(noteService.getNotePage(pageNum, pageSize, moduleType, status));
    }

    @Operation(summary = "获取笔记详情", description = "根据笔记 ID 获取完整内容详情")
    @Parameter(name = "id", description = "笔记 ID", required = true, in = ParameterIn.PATH)
    @GetMapping("/{id}")
    public Result<NoteDetailVO> getDetail(@PathVariable("id") Long id) {
        return Result.success(noteService.getNoteDetail(id));
    }
}
