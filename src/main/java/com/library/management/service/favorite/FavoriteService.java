package com.library.management.service.favorite;

import com.library.management.dto.book.BookResponseDto;

import java.util.List;

public interface FavoriteService {

    List<BookResponseDto> getCurrentUserFavorites();

    BookResponseDto addToFavorites(Long bookId);

    void removeFromFavorites(Long bookId);

    boolean isFavorite(Long bookId);
}