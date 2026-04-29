package com.library.management.mapper;

import com.library.management.dto.book.BookRequestDto;
import com.library.management.dto.book.BookResponseDto;
import com.library.management.entity.Book;
import com.library.management.enums.BookStatus;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthorFullName(dto.getAuthorFullName());
        book.setPublicationYear(dto.getPublicationYear());
        book.setCopiesCount(dto.getCopiesCount());
        book.setGenres(dto.getGenres());
        book.setLanguages(dto.getLanguages());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setDescription(dto.getDescription());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setSplashColor(
                dto.getSplashColor() != null && !dto.getSplashColor().isBlank()
                        ? dto.getSplashColor()
                        : "#d8ddd2"
        );

        if (dto.getCopiesCount() != null && dto.getCopiesCount() == 0) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }

        return book;
    }

    public BookResponseDto toResponseDto(Book book) {
        if (book == null) {
            return null;
        }

        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorFullName(book.getAuthorFullName())
                .publicationYear(book.getPublicationYear())
                .copiesCount(book.getCopiesCount())
                .genres(book.getGenres())
                .languages(book.getLanguages())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .splashColor(book.getSplashColor())
                .status(book.getStatus())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .createdById(book.getCreatedBy() != null ? book.getCreatedBy().getId() : null)
                .createdByName(book.getCreatedBy() != null ? book.getCreatedBy().getFullName() : null)
                .updatedById(book.getUpdatedBy() != null ? book.getUpdatedBy().getId() : null)
                .updatedByName(book.getUpdatedBy() != null ? book.getUpdatedBy().getFullName() : null)
                .build();
    }

    public void updateEntityFromDto(BookRequestDto dto, Book book) {
        book.setTitle(dto.getTitle());
        book.setAuthorFullName(dto.getAuthorFullName());
        book.setPublicationYear(dto.getPublicationYear());
        book.setCopiesCount(dto.getCopiesCount());
        book.setGenres(dto.getGenres());
        book.setLanguages(dto.getLanguages());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setDescription(dto.getDescription());
        book.setCoverImageUrl(dto.getCoverImageUrl());

        if (dto.getSplashColor() != null && !dto.getSplashColor().isBlank()) {
            book.setSplashColor(dto.getSplashColor());
        }

        if (dto.getCopiesCount() != null && dto.getCopiesCount() == 0) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        } else if (book.getStatus() != BookStatus.WRITTEN_OFF) {
            book.setStatus(BookStatus.AVAILABLE);
        }
    }
}