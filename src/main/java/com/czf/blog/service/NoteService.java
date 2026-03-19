package com.czf.blog.service;

import com.czf.blog.common.PageData;
import com.czf.blog.dto.NoteDetailVO;
import com.czf.blog.dto.NoteListVO;
import com.czf.blog.dto.NoteSaveDTO;

/**
 * 笔记服务接口
 * 
 * @author Gemini CLI
 * @date 2026-03-20
 */
public interface NoteService {

    /**
     * 保存或更新笔记
     * 
     * @param dto 笔记保存参数
     * @return 保存后的笔记ID
     */
    Long saveOrUpdateNote(NoteSaveDTO dto);

    /**
     * 分页查询笔记列表
     * 
     * @param pageNum    当前页码
     * @param pageSize   每页大小
     * @param moduleType 模块类型 (0:学习, 1:随笔, 2:兴趣)
     * @param status     状态
     * @return 分页数据
     */
    PageData<NoteListVO> getNotePage(int pageNum, int pageSize, Integer moduleType, Integer status);

    /**
     * 获取笔记详情并增加阅读量
     * 
     * @param id 笔记ID
     * @return 笔记详情
     */
    NoteDetailVO getNoteDetail(Long id);
}
