package com.library.management.dto.book;

import com.library.management.enums.BookStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFilterRequestDto {

    private String keyword;
    private List<String> genres;
    private List<String> languages;
    private BookStatus status;
    private Integer publicationYear;
}