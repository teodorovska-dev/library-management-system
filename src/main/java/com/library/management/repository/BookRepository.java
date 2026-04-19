package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByStatus(BookStatus status);

    List<Book> findAllByStatusNotOrderByAuthorSurnameAsc(BookStatus status);

    List<Book> findAllByStatusNotOrderByPublicationYearAsc(BookStatus status);

    Page<Book> findAllByStatusNot(BookStatus status, Pageable pageable);

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