package com.library.management.controller.book;

import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;
import com.library.management.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public BookResponseDto createBook(@Valid @RequestBody BookRequestDto requestDto) {
        return bookService.createBook(requestDto);
    }

    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/{id}")
    public BookResponseDto updateBook(@PathVariable Long id,
                                      @Valid @RequestBody BookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void writeOffBook(@PathVariable Long id) {
        bookService.writeOffBook(id);
    }

    @GetMapping("/sorted/author")
    public List<BookResponseDto> getBooksSortedByAuthor() {
        return bookService.getBooksSortedByAuthor();
    }

    @GetMapping("/sorted/year")
    public List<BookResponseDto> getBooksSortedByPublicationYear() {
        return bookService.getBooksSortedByPublicationYear();
    }
}