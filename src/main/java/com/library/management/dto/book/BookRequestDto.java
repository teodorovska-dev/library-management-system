package com.library.management.dto.book;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 120, message = "Title must be between 2 and 120 characters")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}\\s\\-:,.!?()'\"&]+$",
            message = "Title contains invalid characters"
    )
    private String title;

    @NotBlank(message = "Author full name is required")
    @Size(min = 2, max = 150, message = "Author full name must be between 2 and 150 characters")
    @Pattern(
            regexp = "^[\\p{L}\\s.'-]+$",
            message = "Author full name may contain only letters, spaces, dots, apostrophes and hyphens"
    )
    private String authorFullName;

    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Publication year must contain 4 digits")
    @Max(value = 2026, message = "Publication year cannot be in the future")
    private Integer publicationYear;

    @NotNull(message = "Copies count is required")
    @Min(value = 0, message = "Copies count cannot be negative")
    @Max(value = 10000, message = "Copies count is too large")
    private Integer copiesCount;

    @NotEmpty(message = "At least one genre is required")
    private List<
            @NotBlank(message = "Genre cannot be blank")
            @Size(min = 2, max = 60, message = "Genre must be between 2 and 60 characters")
            @Pattern(
                    regexp = "^[\\p{L}\\s-]+$",
                    message = "Genre may contain only letters, spaces and hyphens"
            )
                    String
            > genres;

    @NotEmpty(message = "At least one language is required")
    private List<
            @NotBlank(message = "Language cannot be blank")
            @Size(min = 2, max = 60, message = "Language must be between 2 and 60 characters")
            @Pattern(
                    regexp = "^[\\p{L}\\s-]+$",
                    message = "Language may contain only letters, spaces and hyphens"
            )
                    String
            > languages;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 20, message = "ISBN must be between 10 and 20 characters")
    @Pattern(
            regexp = "^[0-9-]+$",
            message = "ISBN may contain only digits and hyphens"
    )
    private String isbn;

    @NotBlank(message = "Publisher is required")
    @Size(min = 2, max = 150, message = "Publisher must be between 2 and 150 characters")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}\\s.,'&()-]+$",
            message = "Publisher contains invalid characters"
    )
    private String publisher;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Size(max = 500, message = "Cover image URL is too long")
    private String coverImageUrl;

    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6})$",
            message = "Splash color must be a valid HEX color"
    )
    private String splashColor;
}