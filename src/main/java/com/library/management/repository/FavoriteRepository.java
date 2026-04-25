package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.entity.Favorite;
import com.library.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findAllByUser(User user);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    boolean existsByUserAndBook(User user, Book book);

    long countByUser(User user);

    void deleteByUserAndBook(User user, Book book);
}