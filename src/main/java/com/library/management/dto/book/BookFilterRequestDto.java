package com.library.management.dto.book;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFilterRequestDto {

    private String keyword;
    private String genre;
    private String language;
    private Integer publicationYear;
}