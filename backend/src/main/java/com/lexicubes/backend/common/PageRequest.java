package com.lexicubes.backend.common;

import java.util.Objects;

public class PageRequest {

    private final int pageNumber;

    private final int pageSize;

    public PageRequest(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }

        if (pageSize < 0) {
            throw new IllegalArgumentException("Page size must be non-negative");
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * {@return the offset to be taken according to the underlying page and page size}
     */
    public int getOffset() {
        return pageNumber * pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PageRequest other &&
                pageNumber == other.pageNumber &&
                pageSize == other.pageSize;
    }
}
