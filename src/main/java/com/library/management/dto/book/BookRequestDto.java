package com.library.management.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author full name is required")
    private String authorFullName;

    @NotNull(message = "Publication year is required")
    @Min(value = 1, message = "Publication year must be greater than 0")
    private Integer publicationYear;

    @NotNull(message = "Copies count is required")
    @Min(value = 0, message = "Copies count cannot be negative")
    private Integer copiesCount;

    private List<String> genres;
    private List<String> languages;
    private String isbn;
    private String publisher;
    private String description;
    private String coverImageUrl;
    private String splashColor;
}