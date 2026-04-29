package com.library.management.dto.book;

import com.library.management.enums.BookStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDto {

    private Long id;
    private String title;
    private String authorFullName;
    private Integer publicationYear;
    private Integer copiesCount;
    private List<String> genres;
    private List<String> languages;
    private String isbn;
    private String publisher;
    private String description;
    private String coverImageUrl;
    private String splashColor;
    private BookStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByName;
    private Long updatedById;
    private String updatedByName;
}