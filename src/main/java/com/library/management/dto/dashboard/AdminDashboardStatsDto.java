package com.library.management.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsDto {

    private long totalBooks;
    private long totalTitles;
    private long totalAuthors;
    private long writtenOffBooks;
    private long availableCopies;
}