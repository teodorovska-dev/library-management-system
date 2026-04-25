package com.library.management.controller.favorite;

import com.library.management.dto.book.BookResponseDto;
import com.library.management.service.favorite.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public List<BookResponseDto> getCurrentUserFavorites() {
        return favoriteService.getCurrentUserFavorites();
    }

    @PostMapping("/{bookId}")
    public BookResponseDto addToFavorites(@PathVariable Long bookId) {
        return favoriteService.addToFavorites(bookId);
    }

    @DeleteMapping("/{bookId}")
    public void removeFromFavorites(@PathVariable Long bookId) {
        favoriteService.removeFromFavorites(bookId);
    }

    @GetMapping("/{bookId}/exists")
    public Map<String, Boolean> isFavorite(@PathVariable Long bookId) {
        return Map.of("favorite", favoriteService.isFavorite(bookId));
    }
}