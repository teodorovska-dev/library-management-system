package com.library.management.specification;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> statusNot(BookStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), status);
    }

    public static Specification<Book> hasGenre(String genre) {
        return (root, query, criteriaBuilder) -> {
            if (genre == null || genre.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("genre")), genre.toLowerCase());
        };
    }

    public static Specification<Book> hasLanguage(String language) {
        return (root, query, criteriaBuilder) -> {
            if (language == null || language.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("language")), language.toLowerCase());
        };
    }

    public static Specification<Book> hasPublicationYear(Integer publicationYear) {
        return (root, query, criteriaBuilder) -> {
            if (publicationYear == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("publicationYear"), publicationYear);
        };
    }

    public static Specification<Book> containsKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("authorSurname")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("authorInitials")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("language")), pattern)
            );
        };
    }
}