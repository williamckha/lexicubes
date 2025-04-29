package com.lexicubes.backend.common;

import java.util.List;

public class PageResponse<T> {

    private final List<T> items;

    private final long pageNumber;

    private final long pageSize;

    private final long totalPageCount;

    private final boolean isFirstPage;

    private final boolean isLastPage;

    public PageResponse(List<T> items,
                        long pageNumber,
                        long pageSize,
                        long totalPageCount) {

        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPageCount = totalPageCount;

        isFirstPage = pageNumber == 0;
        isLastPage = pageNumber == totalPageCount - 1;
    }

    public List<T> getItems() {
        return items;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPageCount() {
        return totalPageCount;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }
}
