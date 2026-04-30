package com.library.management.service.dashboard;

import com.library.management.dto.dashboard.AdminDashboardStatsDto;
import com.library.management.dto.dashboard.DashboardTrendDto;
import com.library.management.enums.BookStatus;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.library.management.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public AdminDashboardStatsDto getStats() {
        LocalDateTime previousMonthPoint = LocalDateTime.now().minusMonths(1);

        long currentTotalBooks = bookRepository.countActiveBooks(BookStatus.WRITTEN_OFF);
        long previousTotalBooks = bookRepository.countActiveBooksBefore(BookStatus.WRITTEN_OFF, previousMonthPoint);

        long currentTotalTitles = bookRepository.countDistinctTitles(BookStatus.WRITTEN_OFF);
        long previousTotalTitles = bookRepository.countDistinctTitlesBefore(BookStatus.WRITTEN_OFF, previousMonthPoint);

        long currentTotalAuthors = bookRepository.countDistinctAuthors(BookStatus.WRITTEN_OFF);
        long previousTotalAuthors = bookRepository.countDistinctAuthorsBefore(BookStatus.WRITTEN_OFF, previousMonthPoint);

        long currentWrittenOffBooks = bookRepository.countByStatus(BookStatus.WRITTEN_OFF);
        long previousWrittenOffBooks = bookRepository.countByStatusBefore(BookStatus.WRITTEN_OFF, previousMonthPoint);

        long currentAvailableCopies = bookRepository.countAvailableCopies(BookStatus.AVAILABLE);
        long previousAvailableCopies = bookRepository.countAvailableCopiesBefore(BookStatus.AVAILABLE, previousMonthPoint);

        long currentTotalUsers = userRepository.count();

        return AdminDashboardStatsDto.builder()
                .totalBooks(buildTrend(currentTotalBooks, previousTotalBooks))
                .totalTitles(buildTrend(currentTotalTitles, previousTotalTitles))
                .totalAuthors(buildTrend(currentTotalAuthors, previousTotalAuthors))
                .writtenOffBooks(buildTrend(currentWrittenOffBooks, previousWrittenOffBooks))
                .availableCopies(buildTrend(currentAvailableCopies, previousAvailableCopies))
                .totalUsers(buildTrend(currentTotalUsers, currentTotalUsers))
                .build();
    }

    private DashboardTrendDto buildTrend(long currentValue, long previousValue) {
        if (previousValue == 0) {
            return DashboardTrendDto.builder()
                    .value(currentValue)
                    .trend(currentValue > 0 ? "increase" : "neutral")
                    .change(currentValue > 0 ? 100 : 0)
                    .build();
        }

        double rawChange = ((double) (currentValue - previousValue) / previousValue) * 100;
        int roundedChange = (int) Math.round(Math.abs(rawChange));

        if (rawChange > 0) {
            return DashboardTrendDto.builder()
                    .value(currentValue)
                    .trend("increase")
                    .change(roundedChange)
                    .build();
        }

        if (rawChange < 0) {
            return DashboardTrendDto.builder()
                    .value(currentValue)
                    .trend("decrease")
                    .change(roundedChange)
                    .build();
        }

        return DashboardTrendDto.builder()
                .value(currentValue)
                .trend("neutral")
                .change(0)
                .build();
    }
}