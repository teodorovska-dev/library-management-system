package com.library.management.util;

import java.util.Set;

public final class BookSortFields {

    private BookSortFields() {
    }

    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "title",
            "authorSurname",
            "publicationYear",
            "copiesCount",
            "genre",
            "language",
            "createdAt",
            "updatedAt",
            "status"
    );
}