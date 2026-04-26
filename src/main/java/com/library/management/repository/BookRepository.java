package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByStatus(BookStatus status);

    List<Book> findAllByStatusNotOrderByAuthorFullNameAsc(BookStatus status);

    List<Book> findAllByStatusNotOrderByPublicationYearAsc(BookStatus status);

    Page<Book> findAllByStatusNot(BookStatus status, Pageable pageable);

    long countByStatus(BookStatus status);

    @Query("""
           SELECT b FROM Book b
           WHERE b.status <> :status
           AND (
                LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.authorFullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.language) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
           )
           """)
    Page<Book> searchBooks(
            @Param("status") BookStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
           SELECT DISTINCT b.genre FROM Book b
           WHERE b.genre IS NOT NULL
           AND b.genre <> ''
           AND b.status <> :status
           ORDER BY b.genre ASC
           """)
    List<String> findDistinctGenres(@Param("status") BookStatus status);

    @Query("""
           SELECT DISTINCT b.language FROM Book b
           WHERE b.language IS NOT NULL
           AND b.language <> ''
           AND b.status <> :status
           ORDER BY b.language ASC
           """)
    List<String> findDistinctLanguages(@Param("status") BookStatus status);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.status <> :status")
    long countActiveBooks(@Param("status") BookStatus status);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.status <> :status AND b.createdAt < :date")
    long countActiveBooksBefore(@Param("status") BookStatus status,
                                @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(DISTINCT b.title) FROM Book b WHERE b.status <> :status")
    long countDistinctTitles(@Param("status") BookStatus status);

    @Query("SELECT COUNT(DISTINCT b.title) FROM Book b WHERE b.status <> :status AND b.createdAt < :date")
    long countDistinctTitlesBefore(@Param("status") BookStatus status,
                                   @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(DISTINCT b.authorFullName) FROM Book b WHERE b.status <> :status")
    long countDistinctAuthors(@Param("status") BookStatus status);

    @Query("SELECT COUNT(DISTINCT b.authorFullName) FROM Book b WHERE b.status <> :status AND b.createdAt < :date")
    long countDistinctAuthorsBefore(@Param("status") BookStatus status,
                                    @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = :status AND b.updatedAt < :date")
    long countByStatusBefore(@Param("status") BookStatus status,
                             @Param("date") LocalDateTime date);

    @Query("SELECT COALESCE(SUM(b.copiesCount), 0) FROM Book b WHERE b.status = :status")
    long countAvailableCopies(@Param("status") BookStatus status);

    @Query("SELECT COALESCE(SUM(b.copiesCount), 0) FROM Book b WHERE b.status = :status AND b.updatedAt < :date")
    long countAvailableCopiesBefore(@Param("status") BookStatus status,
                                    @Param("date") LocalDateTime date);
}