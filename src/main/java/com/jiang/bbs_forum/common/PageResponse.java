package com.jiang.bbs_forum.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页返回对象
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 避免再写 pages
     */
    public PageResponse(long total, List<T> list, int page, int size) {
        this.total = total;
        this.list = list;
        this.page = page;
        this.size = size;

        this.pages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }
}