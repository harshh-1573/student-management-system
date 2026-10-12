package com.student.management.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int pageNo, int pageSize, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public static <E, T> PageResponse<T> of(Page<E> page, List<T> mappedContent) {
        return new PageResponse<>(
                mappedContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<>();
    }

    public static class PageResponseBuilder<T> {
        private List<T> content;
        private int pageNo;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;

        public PageResponseBuilder<T> content(List<T> content) { this.content = content; return this; }
        public PageResponseBuilder<T> pageNo(int pageNo) { this.pageNo = pageNo; return this; }
        public PageResponseBuilder<T> pageSize(int pageSize) { this.pageSize = pageSize; return this; }
        public PageResponseBuilder<T> totalElements(long totalElements) { this.totalElements = totalElements; return this; }
        public PageResponseBuilder<T> totalPages(int totalPages) { this.totalPages = totalPages; return this; }
        public PageResponseBuilder<T> last(boolean last) { this.last = last; return this; }

        public PageResponse<T> build() {
            return new PageResponse<>(content, pageNo, pageSize, totalElements, totalPages, last);
        }
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
