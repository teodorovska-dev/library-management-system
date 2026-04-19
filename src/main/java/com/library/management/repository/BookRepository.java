package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAllByStatusNot(BookStatus status, Pageable pageable);

    List<Book> findAllByStatusNotOrderByAuthorSurnameAsc(BookStatus status);

    List<Book> findAllByStatusNotOrderByPublicationYearAsc(BookStatus status);

    Page<Book> findByStatusNotAndTitleContainingIgnoreCaseOrStatusNotAndAuthorSurnameContainingIgnoreCase(
            BookStatus status1, String title,
            BookStatus status2, String authorSurname,
            Pageable pageable
    );

    Page<Book> findByStatusNotAndGenreIgnoreCase(
            BookStatus status,
            String genre,
            Pageable pageable
    );

    Page<Book> findByStatusNotAndLanguageIgnoreCase(
            BookStatus status,
            String language,
            Pageable pageable
    );

    Page<Book> findByStatusNotAndPublicationYear(
            BookStatus status,
            Integer publicationYear,
            Pageable pageable
    );

    Page<Book> findByStatusNotAndGenreIgnoreCaseAndLanguageIgnoreCase(
            BookStatus status,
            String genre,
            String language,
            Pageable pageable
    );

    @Query("""
       SELECT b FROM Book b
       WHERE b.status <> :status
       AND (
            LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.authorSurname) LIKE LOWER(CONCAT('%', :keyword, '%'))
       )
       """)
    Page<Book> searchBooks(
            @Param("status") BookStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}