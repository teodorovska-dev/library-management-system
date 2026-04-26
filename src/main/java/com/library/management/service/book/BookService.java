package com.library.management.service.book;

import com.library.management.dto.book.BookFilterRequestDto;
import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;
import com.library.management.dto.common.PagedResponseDto;

import java.util.List;

public interface BookService {

    BookResponseDto createBook(BookRequestDto requestDto);

    List<BookResponseDto> getAllBooks();

    BookResponseDto getBookById(Long id);

    BookResponseDto updateBook(Long id, BookRequestDto requestDto);

    void writeOffBook(Long id);

    List<BookResponseDto> getBooksSortedByAuthor();

    List<BookResponseDto> getBooksSortedByPublicationYear();

    PagedResponseDto<BookResponseDto> getBooksPaginated(int page, int size, String sortBy, String sortDir);

    PagedResponseDto<BookResponseDto> searchBooks(String keyword, int page, int size, String sortBy, String sortDir);

    PagedResponseDto<BookResponseDto> filterBooks(BookFilterRequestDto filterRequestDto,
                                                  int page,
                                                  int size,
                                                  String sortBy,
                                                  String sortDir);

    PagedResponseDto<BookResponseDto> getTrendingBooks(int page, int size);

    List<String> getAvailableGenres();

    List<String> getAvailableLanguages();
}