package com.library.management.specification;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;
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

            query.distinct(true);

            Join<Book, String> genreJoin = root.join("genres");

            return criteriaBuilder.lower(genreJoin).in(normalizedGenres);
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

            query.distinct(true);

            Join<Book, String> languageJoin = root.join("languages");

            return criteriaBuilder.lower(languageJoin).in(normalizedLanguages);
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

            Subquery<String> genreSubquery = query.subquery(String.class);
            var genreBookRoot = genreSubquery.from(Book.class);
            Join<Book, String> genreJoin = genreBookRoot.join("genres");

            genreSubquery.select(genreJoin)
                    .where(
                            criteriaBuilder.equal(genreBookRoot.get("id"), root.get("id")),
                            criteriaBuilder.like(criteriaBuilder.lower(genreJoin), pattern)
                    );

            Subquery<String> languageSubquery = query.subquery(String.class);
            var languageBookRoot = languageSubquery.from(Book.class);
            Join<Book, String> languageJoin = languageBookRoot.join("languages");

            languageSubquery.select(languageJoin)
                    .where(
                            criteriaBuilder.equal(languageBookRoot.get("id"), root.get("id")),
                            criteriaBuilder.like(criteriaBuilder.lower(languageJoin), pattern)
                    );

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("authorFullName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), pattern),
                    criteriaBuilder.exists(genreSubquery),
                    criteriaBuilder.exists(languageSubquery)
            );
        };
    }
}