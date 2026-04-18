package com.library.management.service.book;

import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;

import java.util.List;

public interface BookService {

    BookResponseDto createBook(BookRequestDto requestDto);

    List<BookResponseDto> getAllBooks();

    BookResponseDto getBookById(Long id);

    BookResponseDto updateBook(Long id, BookRequestDto requestDto);

    void writeOffBook(Long id);

    List<BookResponseDto> getBooksSortedByAuthor();

    List<BookResponseDto> getBooksSortedByPublicationYear();
}