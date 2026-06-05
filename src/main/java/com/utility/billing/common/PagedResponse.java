package com.utility.billing.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagedResponse<T> {
    private final List<T> content;
    private final PageMetadata meta;

    public PagedResponse(List<T> content, Page<?> page) {
        this.content = content;
        this.meta = new PageMetadata(page);
    }

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(page.getContent(), page);
    }

    public static <T, S> PagedResponse<T> of(List<T> content, Page<S> page) {
        return new PagedResponse<>(content, page);
    }

    @Getter
    public static class PageMetadata {
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean first;
        private final boolean last;
        private final boolean empty;

        public PageMetadata(Page<?> page) {
            this.page = page.getNumber();
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.first = page.isFirst();
            this.last = page.isLast();
            this.empty = page.isEmpty();
        }
    }
}
