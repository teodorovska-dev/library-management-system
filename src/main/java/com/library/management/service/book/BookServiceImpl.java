package com.library.management.service.book;

import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;
import com.library.management.entity.Book;
import com.library.management.entity.User;
import com.library.management.enums.BookStatus;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.BookMapper;
import com.library.management.repository.BookRepository;
import com.library.management.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public BookResponseDto createBook(BookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);

        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        book.setCreatedBy(currentUser);
        book.setUpdatedBy(currentUser);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponseDto(savedBook);
    }

    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getStatus() != BookStatus.WRITTEN_OFF)
                .map(bookMapper::toResponseDto)
                .toList();
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        Book book = findBookByIdOrThrow(id);
        return bookMapper.toResponseDto(book);
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto requestDto) {
        Book existingBook = findBookByIdOrThrow(id);

        bookMapper.updateEntityFromDto(requestDto, existingBook);

        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        existingBook.setUpdatedBy(currentUser);

        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toResponseDto(updatedBook);
    }

    @Override
    public void writeOffBook(Long id) {
        Book book = findBookByIdOrThrow(id);

        User currentUser = currentUserService.getCurrentAuthenticatedUser();
        book.setUpdatedBy(currentUser);
        book.setStatus(BookStatus.WRITTEN_OFF);

        bookRepository.save(book);
    }

    @Override
    public List<BookResponseDto> getBooksSortedByAuthor() {
        return bookRepository.findAllByStatusNotOrderByAuthorSurnameAsc(BookStatus.WRITTEN_OFF)
                .stream()
                .map(bookMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<BookResponseDto> getBooksSortedByPublicationYear() {
        return bookRepository.findAllByStatusNotOrderByPublicationYearAsc(BookStatus.WRITTEN_OFF)
                .stream()
                .map(bookMapper::toResponseDto)
                .toList();
    }

    private Book findBookByIdOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found"));
    }
}