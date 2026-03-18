package com.czf.blog.common;

import lombok.Data;
import java.util.List;

/**
 * 分页数据封装
 * @param <T> 数据类型
 */
@Data
public class PageData<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;

    public PageData() {}

    public PageData(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageData<T> of(List<T> list, long total, int pageNum, int pageSize) {
        return new PageData<>(list, total, pageNum, pageSize);
    }
}
