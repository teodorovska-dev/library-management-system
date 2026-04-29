package com.library.management.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsDto {

    private DashboardTrendDto totalBooks;
    private DashboardTrendDto totalTitles;
    private DashboardTrendDto totalAuthors;
    private DashboardTrendDto writtenOffBooks;
    private DashboardTrendDto availableCopies;
    private DashboardTrendDto totalUsers;
}