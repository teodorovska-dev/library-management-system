package com.library.management.controller.book;

import com.library.management.dto.book.BookFilterRequestDto;
import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;
import com.library.management.dto.common.PagedResponseDto;
import com.library.management.enums.BookStatus;
import com.library.management.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto createBook(@Valid @RequestBody BookRequestDto requestDto) {
        return bookService.createBook(requestDto);
    }

    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/genres")
    public List<String> getAvailableGenres() {
        return bookService.getAvailableGenres();
    }

    @GetMapping("/languages")
    public List<String> getAvailableLanguages() {
        return bookService.getAvailableLanguages();
    }

    @GetMapping("/trending")
    public PagedResponseDto<BookResponseDto> getTrendingBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return bookService.getTrendingBooks(page, size);
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto updateBook(@PathVariable Long id,
                                      @Valid @RequestBody BookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void writeOffBook(@PathVariable Long id) {
        bookService.writeOffBook(id);
    }

    @GetMapping("/paged")
    public PagedResponseDto<BookResponseDto> getBooksPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return bookService.getBooksPaginated(page, size, sortBy, sortDir);
    }

    @GetMapping("/search")
    public PagedResponseDto<BookResponseDto> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return bookService.searchBooks(keyword, page, size, sortBy, sortDir);
    }

    @GetMapping("/filter")
    public PagedResponseDto<BookResponseDto> filterBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) BookStatus status,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        BookFilterRequestDto filterRequestDto = BookFilterRequestDto.builder()
                .keyword(keyword)
                .genres(genres)
                .languages(languages)
                .status(status)
                .publicationYear(publicationYear)
                .build();

        return bookService.filterBooks(filterRequestDto, page, size, sortBy, sortDir);
    }
}