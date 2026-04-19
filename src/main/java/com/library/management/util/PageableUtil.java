package com.library.management.util;

import com.library.management.exception.InvalidRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableUtil {

    private PageableUtil() {
    }

    public static Pageable buildPageable(int page, int size, String sortBy, String sortDir, Set<String> allowedSortFields) {
        validatePage(page);
        validateSize(size);
        validateSortBy(sortBy, allowedSortFields);

        Sort sort = buildSort(sortBy, sortDir);
        return PageRequest.of(page, size, sort);
    }

    private static Sort buildSort(String sortBy, String sortDir) {
        if ("desc".equalsIgnoreCase(sortDir)) {
            return Sort.by(sortBy).descending();
        }

        return Sort.by(sortBy).ascending();
    }

    private static void validatePage(int page) {
        if (page < 0) {
            throw new InvalidRequestException("Page number cannot be negative");
        }
    }

    private static void validateSize(int size) {
        if (size <= 0 || size > 100) {
            throw new InvalidRequestException("Page size must be between 1 and 100");
        }
    }

    private static void validateSortBy(String sortBy, Set<String> allowedSortFields) {
        if (!allowedSortFields.contains(sortBy)) {
            throw new InvalidRequestException("Invalid sort field: " + sortBy);
        }
    }
}