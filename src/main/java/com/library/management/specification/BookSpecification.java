package com.library.management.specification;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> statusNot(BookStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), status);
    }

    public static Specification<Book> hasStatus(BookStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Book> hasGenres(List<String> genres) {
        return (root, query, criteriaBuilder) -> {
            if (genres == null || genres.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<String> normalizedGenres = genres.stream()
                    .filter(genre -> genre != null && !genre.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            if (normalizedGenres.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lower(root.get("genre")).in(normalizedGenres);
        };
    }

    public static Specification<Book> hasLanguages(List<String> languages) {
        return (root, query, criteriaBuilder) -> {
            if (languages == null || languages.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<String> normalizedLanguages = languages.stream()
                    .filter(language -> language != null && !language.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            if (normalizedLanguages.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lower(root.get("language")).in(normalizedLanguages);
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
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("authorFullName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("language")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), pattern)
            );
        };
    }
}