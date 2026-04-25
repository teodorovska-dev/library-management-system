package com.library.management.service.dashboard;

import com.library.management.dto.dashboard.AdminDashboardStatsDto;
import com.library.management.enums.BookStatus;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final BookRepository bookRepository;

    @Override
    public AdminDashboardStatsDto getStats() {
        return AdminDashboardStatsDto.builder()
                .totalBooks(bookRepository.countActiveBooks(BookStatus.WRITTEN_OFF))
                .totalTitles(bookRepository.countDistinctTitles(BookStatus.WRITTEN_OFF))
                .totalAuthors(bookRepository.countDistinctAuthors(BookStatus.WRITTEN_OFF))
                .writtenOffBooks(bookRepository.countByStatus(BookStatus.WRITTEN_OFF))
                .availableCopies(bookRepository.countAvailableCopies(BookStatus.AVAILABLE))
                .build();
    }
}