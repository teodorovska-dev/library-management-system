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
import com.library.management.dto.book.BookFilterRequestDto;
import com.library.management.specification.BookSpecification;
import com.library.management.util.BookSortFields;
import com.library.management.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
        Pageable pageable = PageableUtil.buildPageable(
                page,
                size,
                sortBy,
                sortDir,
                BookSortFields.ALLOWED_SORT_FIELDS
        );

        Page<Book> booksPage = bookRepository.findAllByStatusNot(BookStatus.WRITTEN_OFF, pageable);

        return mapToPagedResponse(booksPage);
    }

    @Override
    public PagedResponseDto<BookResponseDto> searchBooks(String keyword, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageableUtil.buildPageable(
                page,
                size,
                sortBy,
                sortDir,
                BookSortFields.ALLOWED_SORT_FIELDS
        );

        Page<Book> booksPage = bookRepository.searchBooks(BookStatus.WRITTEN_OFF, keyword, pageable);

        return mapToPagedResponse(booksPage);
    }

    @Override
    public PagedResponseDto<BookResponseDto> filterBooks(BookFilterRequestDto filterRequestDto,
                                                         int page,
                                                         int size,
                                                         String sortBy,
                                                         String sortDir) {
        Pageable pageable = PageableUtil.buildPageable(
                page,
                size,
                sortBy,
                sortDir,
                BookSortFields.ALLOWED_SORT_FIELDS
        );

        Specification<Book> specification = Specification
                .where(BookSpecification.statusNot(BookStatus.WRITTEN_OFF))
                .and(BookSpecification.containsKeyword(filterRequestDto.getKeyword()))
                .and(BookSpecification.hasGenre(filterRequestDto.getGenre()))
                .and(BookSpecification.hasLanguage(filterRequestDto.getLanguage()))
                .and(BookSpecification.hasPublicationYear(filterRequestDto.getPublicationYear()));

        Page<Book> booksPage = bookRepository.findAll(specification, pageable);

        return mapToPagedResponse(booksPage);
    }

    private PagedResponseDto<BookResponseDto> mapToPagedResponse(Page<Book> booksPage) {
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