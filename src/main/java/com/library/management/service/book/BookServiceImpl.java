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
import com.library.management.dto.common.PagedResponseDto;
import org.springframework.data.domain.*;

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

    @Override
    public PagedResponseDto<BookResponseDto> getBooksPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = bookRepository.findAllByStatusNot(BookStatus.WRITTEN_OFF, pageable);

        return PagedResponseDto.<BookResponseDto>builder()
                .content(booksPage.getContent().stream().map(bookMapper::toResponseDto).toList())
                .page(booksPage.getNumber())
                .size(booksPage.getSize())
                .totalElements(booksPage.getTotalElements())
                .totalPages(booksPage.getTotalPages())
                .last(booksPage.isLast())
                .build();
    }

    @Override
    public PagedResponseDto<BookResponseDto> searchBooks(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = bookRepository.searchBooks(BookStatus.WRITTEN_OFF, keyword, pageable);

        return PagedResponseDto.<BookResponseDto>builder()
                .content(booksPage.getContent().stream().map(bookMapper::toResponseDto).toList())
                .page(booksPage.getNumber())
                .size(booksPage.getSize())
                .totalElements(booksPage.getTotalElements())
                .totalPages(booksPage.getTotalPages())
                .last(booksPage.isLast())
                .build();
    }

    @Override
    public PagedResponseDto<BookResponseDto> filterBooks(String genre, String language, Integer publicationYear,
                                                         int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage;

        if (genre != null && language != null) {
            booksPage = bookRepository.findByStatusNotAndGenreIgnoreCaseAndLanguageIgnoreCase(
                    BookStatus.WRITTEN_OFF, genre, language, pageable
            );
        } else if (genre != null) {
            booksPage = bookRepository.findByStatusNotAndGenreIgnoreCase(
                    BookStatus.WRITTEN_OFF, genre, pageable
            );
        } else if (language != null) {
            booksPage = bookRepository.findByStatusNotAndLanguageIgnoreCase(
                    BookStatus.WRITTEN_OFF, language, pageable
            );
        } else if (publicationYear != null) {
            booksPage = bookRepository.findByStatusNotAndPublicationYear(
                    BookStatus.WRITTEN_OFF, publicationYear, pageable
            );
        } else {
            booksPage = bookRepository.findAllByStatusNot(BookStatus.WRITTEN_OFF, pageable);
        }

        return PagedResponseDto.<BookResponseDto>builder()
                .content(booksPage.getContent().stream().map(bookMapper::toResponseDto).toList())
                .page(booksPage.getNumber())
                .size(booksPage.getSize())
                .totalElements(booksPage.getTotalElements())
                .totalPages(booksPage.getTotalPages())
                .last(booksPage.isLast())
                .build();
    }
}