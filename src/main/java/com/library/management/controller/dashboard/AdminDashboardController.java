package com.library.management.controller.dashboard;

import com.library.management.dto.dashboard.AdminDashboardStatsDto;
import com.library.management.service.dashboard.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardStatsDto getStats() {
        return adminDashboardService.getStats();
    }
}