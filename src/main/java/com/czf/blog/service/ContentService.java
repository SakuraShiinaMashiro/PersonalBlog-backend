package com.czf.blog.service;

import com.czf.blog.common.PageData;
import com.czf.blog.dto.ContentDetailVO;
import com.czf.blog.dto.ContentListVO;
import com.czf.blog.dto.ContentSaveDTO;

/**
 * 内容服务接口
 * 
 * @author Gemini
 * @date 2026-03-18
 */
public interface ContentService {

    /**
     * 保存或更新内容
     * 
     * @param dto 内容保存参数
     * @return 保存后的内容ID
     */
    Long saveOrUpdateContent(ContentSaveDTO dto);

    /**
     * 分页查询内容列表
     * 
     * @param pageNum    当前页码
     * @param pageSize   每页大小
     * @param moduleType 模块类型 (0:学习, 1:随笔, 2:兴趣)
     * @param status     状态
     * @return 分页数据
     */
    PageData<ContentListVO> getContentPage(int pageNum, int pageSize, Integer moduleType, Integer status);

    /**
     * 获取内容详情并增加阅读量
     * 
     * @param id 内容ID
     * @return 内容详情
     */
    ContentDetailVO getContentDetail(Long id);
}
