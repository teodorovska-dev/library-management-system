package com.library.management.service.favorite;

import com.library.management.dto.book.BookResponseDto;
import com.library.management.entity.Book;
import com.library.management.entity.Favorite;
import com.library.management.entity.User;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.BookMapper;
import com.library.management.repository.BookRepository;
import com.library.management.repository.FavoriteRepository;
import com.library.management.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<BookResponseDto> getCurrentUserFavorites() {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();

        return favoriteRepository.findAllByUser(currentUser)
                .stream()
                .map(Favorite::getBook)
                .map(bookMapper::toResponseDto)
                .toList();
    }

    @Override
    public BookResponseDto addToFavorites(Long bookId) {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        Book book = findBookByIdOrThrow(bookId);

        if (!favoriteRepository.existsByUserAndBook(currentUser, book)) {
            Favorite favorite = Favorite.builder()
                    .user(currentUser)
                    .book(book)
                    .build();

            favoriteRepository.save(favorite);
        }

        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional
    public void removeFromFavorites(Long bookId) {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        Book book = findBookByIdOrThrow(bookId);

        favoriteRepository.deleteByUserAndBook(currentUser, book);
    }

    @Override
    public boolean isFavorite(Long bookId) {
        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        Book book = findBookByIdOrThrow(bookId);

        return favoriteRepository.existsByUserAndBook(currentUser, book);
    }

    private Book findBookByIdOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + bookId + " not found"));
    }
}