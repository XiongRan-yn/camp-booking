package com.example.campbooking.common;

import lombok.Data;

@Data
public class PageResult<T> {
    private java.util.List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    public static <T> PageResult<T> of(java.util.List<T> list, Long total, Integer page, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }
}
