package com.library.management.util;

import java.util.Map;
import java.util.Set;

public final class BookSortFields {

    private BookSortFields() {
    }

    public static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "title", "title",
            "author", "authorSurname",
            "year", "publicationYear",
            "category", "genre",
            "language", "language",
            "status", "status",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    public static final Set<String> ALLOWED_FRONTEND_SORT_FIELDS = SORT_FIELD_MAPPING.keySet();

    public static final Set<String> ALLOWED_BACKEND_SORT_FIELDS = Set.copyOf(SORT_FIELD_MAPPING.values());

    public static String resolveSortField(String frontendSortField) {
        if (frontendSortField == null || frontendSortField.isBlank()) {
            return "title";
        }

        return SORT_FIELD_MAPPING.getOrDefault(frontendSortField, "title");
    }
}