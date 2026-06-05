package com.utility.billing.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PaginationUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIR = "asc";

    private PaginationUtil() {}

    public static Pageable toPageable(Integer page, Integer size, String sortBy, String sortDir, Set<String> allowedSortFields) {
        int pageVal = (page == null || page < 0) ? DEFAULT_PAGE : page;
        int sizeVal = (size == null || size <= 0) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        
        String sortByVal = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT_BY : sortBy;
        if (allowedSortFields != null && !allowedSortFields.isEmpty() && !allowedSortFields.contains(sortByVal)) {
            sortByVal = DEFAULT_SORT_BY;
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir != null && (sortDir.equalsIgnoreCase("desc") || sortDir.equalsIgnoreCase("descending"))) {
            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(pageVal, sizeVal, Sort.by(direction, sortByVal));
    }
}
